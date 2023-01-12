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
import pl.newsler.auth.JWTUtility;
import pl.newsler.security.filters.JWTFilter;

@ComponentScan
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableGlobalAuthentication
@RequiredArgsConstructor
class SecurityConfiguration {
    private final DatabaseUserDetailService userDetailService;
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
                .addFilterBefore(new JWTFilter("/v1/auth/jwt", manager, userDetailService, jwtUtility), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
