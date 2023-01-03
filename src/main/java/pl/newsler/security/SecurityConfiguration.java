package pl.newsler.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import pl.newsler.auth.CustomAuthenticationProvider;
import pl.newsler.auth.JWTUtility;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.filters.JWTFilter;

@ComponentScan
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
class SecurityConfiguration {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JWTUtility jwtUtility;
    private final IUserRepository userRepository;
    private final CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    public AuthenticationManager authManager() {
        return authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider).getOrBuild();
    }

    @Bean(name = "securityFilterChain")
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests()
                .requestMatchers("/v1/api/jwt").permitAll()
                .anyRequest().authenticated();
//                .withObjectPostProcessor();

        http.addFilterAfter(new JWTFilter(authenticationManagerBuilder.getOrBuild(), userRepository, jwtUtility), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
