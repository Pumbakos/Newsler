package pl.newsler.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.newsler.config.security.PasswordEncoder;

@Configuration
@NoArgsConstructor
public class JWTConfiguration {
    @Bean
    public Algorithm hmac384(){
        return Algorithm.HMAC384(PasswordEncoder.salt());
    }

    @Bean
    public JWTCreator.Builder builder() {
        return JWT.create();
    }
}
