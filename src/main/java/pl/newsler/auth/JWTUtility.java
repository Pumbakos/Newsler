package pl.newsler.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import pl.newsler.security.NLPasswordEncoder;

@Component
@NoArgsConstructor
public class JWTUtility {
    public Algorithm hmac384() {
        return Algorithm.HMAC384(NLPasswordEncoder.salt());
    }

    public JWTCreator.Builder builder() {
        return JWT.create();
    }
}
