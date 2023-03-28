package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.NLIKeyProvider;
import pl.newsler.security.NLIPasswordEncoder;

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
    IJWTAuthService jwtAuthService(JWTUtility utility, AuthUserDetailService authUserDetailService) {
        return new JWTAuthService(userRepository, passwordEncoder, authUserDetailService, utility);
    }

    @Bean(name = "authenticationProvider")
    AuthenticationProvider authenticationProvider() {
        return new NLAuthenticationProvider(userRepository);
    }

    @Bean(name = "authUserDetailService")
    AuthUserDetailService authUserDetailService() {
        return new AuthUserDetailService(userRepository);
    }
}
