package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.NLIKeyProvider;
import pl.newsler.security.NLIPasswordEncoder;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class JWTConfiguration {
    private final IUserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;
    private final NLIKeyProvider keyProvider;

    @Bean(name = "jwtUtility")
    public JWTUtility jwtUtility() {
        return new JWTUtility(keyProvider);
    }

    @Bean(name = "authService")
    public JWTAuthService authService(JWTUtility utility) {
        return new JWTAuthService(userRepository, passwordEncoder, utility);
    }
}
