package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.NLIKeyProvider;
import pl.newsler.security.NLIPasswordEncoder;
import pl.newsler.security.filters.JWTFilter;

@ComponentScan
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
class JWTConfiguration {
    private final IUserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;
    private final NLIKeyProvider keyProvider;

    @Bean(name = "jwtUtility")
    JWTUtility jwtUtility() {
        return new JWTUtility(keyProvider);
    }

    @Bean(name = "jwtAuthService")
    IJWTAuthService jwtAuthService(JWTUtility utility) {
        return new JWTAuthService(userRepository, passwordEncoder, utility);
    }

    @Bean(name = "databaseUserDetailService")
    DatabaseUserDetailService databaseUserDetailService() {
        return new DatabaseUserDetailService(userRepository);
    }
}
