package pl.newsler.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pl.newsler.api.UserRepository;
import pl.newsler.exceptions.implemenation.UnauthorizedException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.util.Collections;
import java.util.Set;

public class JWTFilter extends BasicAuthenticationFilter {
    private final UserRepository repository;

    private static final String SALT = "H4p&D*!HDNAS)IFN_)!";

    public JWTFilter(AuthenticationManager authenticationManager, UserRepository repository) {
        super(authenticationManager);
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        final String bearer = request.getHeader(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");
        UsernamePasswordAuthenticationToken authentication = getAuthentication(bearer);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String authToken) {
        final JWTVerifier verifier = JWT.require(Algorithm.HMAC384(SALT)).build();
        final DecodedJWT jwt = verifier.verify(authToken);
        final String email = jwt.getClaim("email").asString();

        if (repository.findByEmail(email).isEmpty()) {
            throw new UnauthorizedException("Invalid email", "Could not find username with provided email");
        } else {
            final String role = jwt.getClaim("role").asString();
            final Set<SimpleGrantedAuthority> roles = Collections.singleton(new SimpleGrantedAuthority(role));
            return new UsernamePasswordAuthenticationToken(email, null, roles);
        }
    }
}
