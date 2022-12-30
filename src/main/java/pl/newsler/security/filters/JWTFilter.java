package pl.newsler.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
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

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private static final String TOKEN = "Token";
    private final AuthenticationManager authenticationManager;
    private final IUserRepository repository;
    private final JWTUtility utility;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain) {
        if (request.getRequestURI().equals("/v1/api/jwt")) {
            try {
                chain.doFilter(request, response);
            } catch (IOException | ServletException e) {
                throw new UnauthorizedException(TOKEN, e.getMessage());
            }
            return;
        }

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(header)) {
            throw new UnauthorizedException("Nullable or empty auth header", "Not authorized");
        }

        final String bearer = header.replace("Bearer ", "");
        final NLAuthenticationToken authentication = getAuthentication(bearer);

        performAuthentication(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
//            chain.doFilter(request, response);
    }

    private void performAuthentication(NLAuthenticationToken authentication) {
        try {
            authenticationManager.authenticate(authentication);
        } catch (DisabledException | BadCredentialsException e) {
            throw new UnauthorizedException(TOKEN, "Invalid");
        }
    }

    private NLAuthenticationToken getAuthentication(String authToken) {
        final DecodedJWT jwt = verifyToken(authToken);
        final String email = jwt.getClaim(JWTClaim.EMAIL).asString();
        final NLEmail nlEmail = NLEmail.of(email);
        if (!nlEmail.validate()) {
            throw new UnauthorizedException("Email", "Invalid email");
        }

        final Optional<NLUser> optionalUser = repository.findByEmail(nlEmail);
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException(TOKEN, "Access denied");
        }

        if (JWTResolver.resolveJWT(jwt)) {
            final NLDUser user = NLDUser.of(optionalUser.get());
            final NLPrincipal principal = createPrincipal(user);
            final NLCredentials credentials = createCredentials(user);
            final Set<SimpleGrantedAuthority> roles = Collections.singleton(new SimpleGrantedAuthority(jwt.getClaim(JWTClaim.ROLE).asString()));

            return new NLAuthenticationToken(principal, credentials, roles);
        }
        throw new UnauthorizedException(TOKEN, "Access denied");
    }

    private DecodedJWT verifyToken(String authToken) {
        final JWTVerifier verifier = JWT.require(utility.hmac384()).build();
        try {
            return verifier.verify(authToken);
        } catch (JWTVerificationException e) {
            throw new UnauthorizedException(TOKEN, "Invalid token");
        }
    }

    private NLPrincipal createPrincipal(NLDUser user) {
        return new NLPrincipal(user.getId(), user.getEmail(), user.getName());
    }

    private NLCredentials createCredentials(NLDUser user) {
        return new NLCredentials(user.getPassword());
    }
}