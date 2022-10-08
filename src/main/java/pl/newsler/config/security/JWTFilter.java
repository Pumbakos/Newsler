package pl.newsler.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pl.newsler.api.User;
import pl.newsler.api.UserRepository;
import pl.newsler.auth.JWTClaim;
import pl.newsler.auth.JWTConfiguration;
import pl.newsler.api.exceptions.UnauthorizedException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class JWTFilter extends BasicAuthenticationFilter {
    private final UserRepository repository;

    private final JWTConfiguration configuration;

    public JWTFilter(AuthenticationManager authenticationManager, UserRepository repository, JWTConfiguration configuration) {
        super(authenticationManager);
        this.repository = repository;
        this.configuration = configuration;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && !header.isEmpty() && !header.isBlank()) {
            String bearer = header.replace("Bearer ", "");
            UsernamePasswordAuthenticationToken authentication = getAuthentication(bearer);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            throw new UnauthorizedException("Nullable or empty auth header", "Not authorized");
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String authToken) {
        final JWTVerifier verifier = JWT.require(configuration.hmac384()).build();
        final DecodedJWT jwt = verifier.verify(authToken);
        final String email = jwt.getClaim(JWTClaim.EMAIL).asString();
        final Optional<User> optionalUser = repository.findByEmail(email);

        if (optionalUser.isPresent() && resolveJWT(jwt)) {
            final String role = jwt.getClaim(JWTClaim.ROLE).asString();
            final Set<SimpleGrantedAuthority> roles = Collections.singleton(new SimpleGrantedAuthority(role));
            //TODO: implement owe Credentials and Principal, maybe own Authentication (AbstractAuthenticationToken)?
            return new UsernamePasswordAuthenticationToken(email, optionalUser.get().getPassword(), roles);
        } else {
            throw new UnauthorizedException("Invalid email", "Could not find username with provided email");
        }
    }

    private boolean resolveJWT(DecodedJWT jwt) {
        final Instant now = Instant.now();
        final String keyId = jwt.getId();
        final String issuer = jwt.getIssuer();
        final Instant expiration = jwt.getExpiresAtAsInstant();

        return (keyId != null && keyId.equals(String.valueOf(JWTClaim.JWT_ID))
                && issuer != null  && issuer.equals(JWTClaim.ISSUER)
                && now != null  && now.isBefore(expiration));
    }
}
