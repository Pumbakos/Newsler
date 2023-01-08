package pl.newsler.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.newsler.auth.DatabaseUserDetailService;
import pl.newsler.auth.Http401UnauthorizedEntryPoint;
import pl.newsler.auth.JWTUtility;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.filters.JWTFilter;

@ComponentScan
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableGlobalAuthentication
@RequiredArgsConstructor
class SecurityConfiguration {
    private final DatabaseUserDetailService userDetailService;
    private final NLIPasswordEncoder passwordEncoder;
    private final Http401UnauthorizedEntryPoint entryPoint = new Http401UnauthorizedEntryPoint();
    private final IUserRepository userRepository;
    private final JWTUtility jwtUtility;

    @Bean(name = "securityFilterChain")
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        AuthenticationManager manager = authenticationManagerBuilder.getObject();
        http
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // temporarily authenticated via JWT
                )
                .exceptionHandling(customizer -> customizer
                        .accessDeniedHandler(entryPoint)
                        .authenticationEntryPoint(entryPoint)
                )
                .addFilterBefore(new JWTFilter("/v1/auth/jwt", manager, userDetailService, jwtUtility), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.
//        authProvider.setUserDetailsService(userDetailService);
//        authProvider.setPasswordEncoder(passwordEncoder.bCrypt());
//
//        return authProvider;
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
