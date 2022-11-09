package pl.newsler.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pl.newsler.api.exceptions.UnauthorizedException;
import pl.newsler.auth.JWTClaim;
import pl.newsler.auth.JWTUtility;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLDUser;
import pl.newsler.components.user.NLUser;
import pl.newsler.security.NLAuthenticationToken;
import pl.newsler.security.NLCredentials;
import pl.newsler.security.NLPrincipal;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class JWTFilter extends BasicAuthenticationFilter {
    private final IUserRepository repository;
    private final JWTUtility utility;

    public JWTFilter(AuthenticationManager authenticationManager, IUserRepository repository, JWTUtility utility) {
        super(authenticationManager);
        this.repository = repository;
        this.utility = utility;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(header)) {
            throw new UnauthorizedException("Nullable or empty auth header", "Not authorized");
        }

        final String bearer = header.replace("Bearer ", "");
        final NLAuthenticationToken authentication = getAuthentication(bearer);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private NLAuthenticationToken getAuthentication(String authToken) {
        final DecodedJWT jwt = verifyToken(authToken);
        final String email = jwt.getClaim(JWTClaim.EMAIL).asString();
        final NLEmail nlEmail = NLEmail.of(email);
        if (nlEmail.validate()) {
            throw new UnauthorizedException("Invalid email", "Email does not mach regular expression");
        }

        final Optional<NLUser> optionalUser = repository.findByEmail(nlEmail);

        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException("Access denied", "");
        }

        if (JWTFilterHelper.resolveJWT(jwt)) {
            final NLDUser user = NLDUser.of(optionalUser.get());
            final NLPrincipal principal = JWTFilterHelper.createPrincipal(user);
            final NLCredentials credentials = JWTFilterHelper.createCredentials(user);
            final Set<SimpleGrantedAuthority> roles = Collections.singleton(new SimpleGrantedAuthority(jwt.getClaim(JWTClaim.ROLE).asString()));

            return new NLAuthenticationToken(principal, credentials, roles);
        }
        throw new UnauthorizedException("Access denied", "");
    }

    private DecodedJWT verifyToken(String authToken) {
        final JWTVerifier verifier = JWT.require(utility.hmac384()).build();
        try {
            return verifier.verify(authToken);
        } catch (JWTVerificationException e) {
            throw new UnauthorizedException("Invalid token", e.getMessage());
        }
    }
}