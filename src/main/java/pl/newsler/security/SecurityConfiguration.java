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
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
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
    private static final String[] AUTH_BLACKLIST = {
            "/v1/api/auth/**",
            "/v1/api/subscription/cancel**"
    };

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
                .addFilterBefore(
                        new JWTFilter(manager, userDetailService, jwtUtility),
                        UsernamePasswordAuthenticationFilter.class
                )
//                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                ;

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // https://medium.com/swlh/stateless-jwt-authentication-with-spring-boot-a-better-approach-1f5dbae6c30f
        return web -> web.ignoring().requestMatchers(AUTH_BLACKLIST);
    }
}
