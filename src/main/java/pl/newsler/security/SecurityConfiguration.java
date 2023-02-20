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
import org.springframework.web.cors.CorsConfiguration;
import pl.newsler.auth.AuthUserDetailService;
import pl.newsler.auth.JWTUtility;
import pl.newsler.security.filter.JWTFilter;

import java.util.List;

@ComponentScan
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableGlobalAuthentication
@RequiredArgsConstructor
class SecurityConfiguration {
    private final AuthUserDetailService userDetailService;
    private final JWTUtility jwtUtility;

    @Bean(name = "securityFilterChain")
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        AuthenticationManager manager = authenticationManagerBuilder.getObject();
        http
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors().configurationSource(request -> {
                    CorsConfiguration cors = new CorsConfiguration();
                    cors.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:80", "https://localhost:4200"));
                    cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    cors.setAllowedHeaders(List.of("*"));
                    return cors;
                }).and()
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // temporarily authenticated via JWT
                )
                .addFilterBefore(new JWTFilter("/v1/api/auth/", manager, userDetailService, jwtUtility), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
