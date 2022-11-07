//package pl.newsler.auth;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import pl.newsler.components.user.definition.NLIUserRepository;
//import pl.newsler.security.NLIKeyProvider;
//import pl.newsler.security.NLPasswordEncoder;
//
//@RequiredArgsConstructor
//@Configuration(proxyBeanMethods = false)
//public class JWTConfiguration {
//    private final NLIUserRepository userRepository;
//    private final NLPasswordEncoder passwordEncoder;
//    private final NLIKeyProvider keyProvider;
//
//    @Bean
//    public JWTAuthService authService(JWTUtility utility) {
//        return new JWTAuthService(userRepository, passwordEncoder, utility);
//    }
//
//    @Bean
//    public JWTUtility jwtUtility() {
//        return new JWTUtility(keyProvider);
//    }
//}
