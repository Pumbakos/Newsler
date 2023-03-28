package pl.newsler.security.filter;

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
import pl.newsler.auth.AuthUserDetailService;
import pl.newsler.auth.JWTClaim;
import pl.newsler.auth.JWTUtility;
import pl.newsler.commons.exception.InvalidTokenException;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.exception.UnauthorizedException;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.components.user.NLUser;
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
    private final String[] notProcessingUrls;

    /**
     * Creates a new instance with a default filterProcessesUrl and an
     * {@link AuthenticationManager}
     *
     * @param notProcessingUrls     urls to be omitted by <tt>JWTFilter</tt>
     * @param authenticationManager the {@link AuthenticationManager} used to authenticate an {@link Authentication} object. Cannot be null.
     * @param authUserDetailService {@link org.springframework.security.core.userdetails.UserDetailsService}
     */
    public JWTFilter(@NotNull AuthenticationManager authenticationManager,
                     @NotNull AuthUserDetailService authUserDetailService, @NotNull JWTUtility utility,
                     String... notProcessingUrls) {
        super();
        if (StringUtils.isAllBlank(notProcessingUrls)) {
            this.notProcessingUrls = new String[]{};
        } else {
            this.notProcessingUrls = notProcessingUrls;
        }
        this.authenticationManager = authenticationManager;
        this.authUserDetailService = authUserDetailService;
        this.utility = utility;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain) throws ServletException, IOException {
        boolean isUrlOnBlacklist = isUrlOnBlacklist(request);
        if (isUrlOnBlacklist) {
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

    private boolean isUrlOnBlacklist(final @NotNull HttpServletRequest request) {
        for (final String url : notProcessingUrls) {
            if (request.getRequestURI().contains(url)) {
                return true;
            }
        }
        return false;
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
            throw new InvalidTokenException("Email", "Invalid email");
        }

        final NLUser user;
        try {
            user = (NLUser) authUserDetailService.loadUserByUsername(email);
        } catch (UsernameNotFoundException | InvalidUserDataException e) {
            throw new InvalidTokenException(TOKEN, "Incorrect credentials.");
        }

        if (JWTResolver.resolveJWT(jwt, user)) {
            final NLPrincipal principal = createPrincipal(user);
            final NLCredentials credentials = createCredentials(user);
            final Set<SimpleGrantedAuthority> roles = Collections.singleton(new SimpleGrantedAuthority(jwt.getClaim(JWTClaim.ROLE).asString()));

            return new NLAuthenticationToken(principal, credentials, roles);
        }
        throw new InvalidTokenException(TOKEN, "Access denied.");
    }

    private @NotNull DecodedJWT verifyToken(@NotNull String authToken) {
        final JWTVerifier verifier = JWT.require(utility.hmac384()).build();
        try {
            return verifier.verify(authToken);
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException(TOKEN, "Invalid token");
        }
    }

    private @NotNull NLPrincipal createPrincipal(@NotNull NLUser user) {
        return new NLPrincipal(user.map().getId(), user.getEmail(), user.getFirstName());
    }

    private @NotNull NLCredentials createCredentials(@NotNull NLUser user) {
        return new NLCredentials(user.getNLPassword());
    }
}