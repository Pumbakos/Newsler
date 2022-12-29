package pl.newsler.security;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.newsler.auth.JWTUtility;
import pl.newsler.components.user.IUserCrudService;
import pl.newsler.components.user.IUserRepository;

@ComponentScan
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfiguration {
//    private final AuthenticationConfiguration authenticationConfiguration;
//
//    @Resource(name = "bCryptPasswordEncoder")
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    private final IUserRepository userRepository;
//
//    @Resource(name = "userService")
//    private final IUserCrudService userService;
//    private final JWTUtility jwtUtility;

    @Bean(name = "securityFilterChain")
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        http.authorizeRequests().anyRequest().authenticated()
//                .and()
//                .addFilter(new JWTFilter(authenticationConfiguration.getAuthenticationManager(), userRepository, jwtUtility));
        //FIXME: https://www.baeldung.com/spring-security-oauth-jwt

        return http.build();
    }
}
