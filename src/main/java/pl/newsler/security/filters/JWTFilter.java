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
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.newsler.commons.exception.InvalidTokenException;
import pl.newsler.commons.exception.UnauthorizedException;
import pl.newsler.auth.AuthUserDetailService;
import pl.newsler.auth.JWTClaim;
import pl.newsler.auth.JWTUtility;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.components.user.NLDUser;
import pl.newsler.components.user.NLUser;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.security.NLAuthenticationToken;
import pl.newsler.security.NLCredentials;
import pl.newsler.security.NLPrincipal;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class JWTFilter extends OncePerRequestFilter {
    private static final String TOKEN = "Token";
    private final AuthenticationManager authenticationManager;
    private final AuthUserDetailService authUserDetailService;
    private final JWTUtility utility;
    private final String filterNotProcessingUrl;

    /**
     * Creates a new instance with a default filterProcessesUrl and an
     * {@link AuthenticationManager}
     *
     * @param filterNotProcessingUrl the default value for <tt>filterProcessesUrl</tt>.
     * @param authenticationManager  the {@link AuthenticationManager} used to authenticate an {@link Authentication} object. Cannot be null.
     * @param authUserDetailService  {@link org.springframework.security.core.userdetails.UserDetailsService}
     */
    public JWTFilter(@NotNull String filterNotProcessingUrl, @NotNull AuthenticationManager authenticationManager,
                     @NotNull AuthUserDetailService authUserDetailService, @NotNull JWTUtility utility) {
        super();
        this.filterNotProcessingUrl = filterNotProcessingUrl;
        this.authenticationManager = authenticationManager;
        this.authUserDetailService = authUserDetailService;
        this.utility = utility;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain) throws ServletException, IOException {
        if (request.getRequestURI().contains(filterNotProcessingUrl)) {
            chain.doFilter(request, response);
            return;
        }

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(header)) {
            throw new InvalidTokenException("Nullable or empty auth header", "Not authorized");
        }

        final String bearer = header.replace("Bearer ", "");
        final NLAuthenticationToken authentication = getAuthentication(bearer);

        performAuthentication(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private void performAuthentication(@NotNull NLAuthenticationToken authentication) {
        try {
            authenticationManager.authenticate(authentication);
        } catch (DisabledException | BadCredentialsException e) {
            throw new UnauthorizedException(TOKEN, "Invalid");
        }
    }

    private @NotNull NLAuthenticationToken getAuthentication(@NotNull String authToken) {
        final DecodedJWT jwt = verifyToken(authToken);
        final String email = jwt.getClaim(JWTClaim.EMAIL).asString();
        final NLEmail nlEmail = NLEmail.of(email);
        if (!nlEmail.validate()) {
            throw new UnauthorizedException("Email", "Invalid email");
        }

        final NLUser user;
        try {
            user = (NLUser) authUserDetailService.loadUserByUsername(email);
        } catch (UsernameNotFoundException | InvalidUserDataException e) {
            throw new UnauthorizedException(TOKEN, "Incorrect credentials.");
        }

        if (JWTResolver.resolveJWT(jwt)) {
            final NLDUser dtoUser = user.map();
            final NLPrincipal principal = createPrincipal(dtoUser);
            final NLCredentials credentials = createCredentials(dtoUser);
            final Set<SimpleGrantedAuthority> roles = Collections.singleton(new SimpleGrantedAuthority(jwt.getClaim(JWTClaim.ROLE).asString()));

            return new NLAuthenticationToken(principal, credentials, roles);
        }
        throw new UnauthorizedException(TOKEN, "Access denied.");
    }

    private @NotNull DecodedJWT verifyToken(@NotNull String authToken) {
        final JWTVerifier verifier = JWT.require(utility.hmac384()).build();
        try {
            return verifier.verify(authToken);
        } catch (JWTVerificationException e) {
            throw new UnauthorizedException(TOKEN, "Invalid token");
        }
    }

    private @NotNull NLPrincipal createPrincipal(@NotNull NLDUser user) {
        return new NLPrincipal(user.getId(), user.getEmail(), user.getName());
    }

    private @NotNull NLCredentials createCredentials(@NotNull NLDUser user) {
        return new NLCredentials(user.getPassword());
    }
}