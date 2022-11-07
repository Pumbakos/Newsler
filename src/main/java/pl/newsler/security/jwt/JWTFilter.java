//package pl.newsler.security.jwt;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.JWTVerifier;
//import com.auth0.jwt.exceptions.JWTVerificationException;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//import pl.newsler.api.exceptions.UnauthorizedException;
//import pl.newsler.auth.JWTClaim;
//import pl.newsler.auth.JWTUtility;
//import pl.newsler.commons.models.NLEmail;
//import pl.newsler.components.user.NLDUser;
//import pl.newsler.security.NLAuthenticationToken;
//import pl.newsler.security.NLCredentials;
//import pl.newsler.security.NLPrincipal;
//
//import javax.servlet.FilterChain;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.ws.rs.core.HttpHeaders;
//import java.util.Collections;
//import java.util.Optional;
//import java.util.Set;
//
//public class JWTFilter extends BasicAuthenticationFilter {
//    private final IUserRepository repository;
//
//    private final JWTUtility configuration;
//
//    public JWTFilter(AuthenticationManager authenticationManager, IUserRepository repository, JWTUtility configuration) {
//        super(authenticationManager);
//        this.repository = repository;
//        this.configuration = configuration;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
//        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (header != null && !header.isEmpty() && !header.isBlank()) {
//            String bearer = header.replace("Bearer ", "");
//            NLAuthenticationToken authentication = getAuthentication(bearer);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } else {
//            throw new UnauthorizedException("Nullable or empty auth header", "Not authorized");
//        }
//    }
//
//    private NLAuthenticationToken getAuthentication(String authToken) {
//        final DecodedJWT jwt = verifyToken(authToken);
//        final String email = jwt.getClaim(JWTClaim.EMAIL).asString();
//        final Optional<NLDUser> optionalUser = repository.getByEmail(NLEmail.of(email));
//
//        if (optionalUser.isPresent() && JWTFilterHelper.resolveJWT(jwt)) {
//            final NLDUser user = optionalUser.get();
//
//            final NLPrincipal principal = JWTFilterHelper.createPrincipal(user);
//            final NLCredentials credentials = JWTFilterHelper.createCredentials(user);
//            final Set<SimpleGrantedAuthority> roles = Collections.singleton(new SimpleGrantedAuthority(jwt.getClaim(JWTClaim.ROLE).asString()));
//
//            return new NLAuthenticationToken(principal, credentials, roles);
//        } else {
//            throw new UnauthorizedException("Invalid email", "Could not find username with provided email");
//        }
//    }
//
//    private DecodedJWT verifyToken(String authToken) {
//        final JWTVerifier verifier = JWT.require(configuration.hmac384()).build();
//        try {
//            return verifier.verify(authToken);
//        } catch (JWTVerificationException e) {
//            throw new UnauthorizedException("Invalid token", e.getMessage());
//        }
//    }
//}