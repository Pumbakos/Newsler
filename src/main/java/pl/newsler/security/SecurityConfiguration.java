package pl.newsler.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.newsler.auth.CustomAuthenticationProvider;
import pl.newsler.auth.DatabaseUserDetailService;
import pl.newsler.auth.Http401UnauthorizedEntryPoint;
import pl.newsler.auth.JWTUtility;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.filters.JWTFilter;

@ComponentScan
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfiguration {
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final DatabaseUserDetailService userDetailService;
    private final NLIPasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final JWTUtility jwtUtility;

    @Bean(name = "securityFilterChain")
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        AuthenticationManager builderObject = authenticationManagerBuilder.getObject();
//        AuthenticationManager newManager = new
//                .authenticationProvider(customAuthenticationProvider)
//                .userDetailsService(userDetailService)
//                .passwordEncoder(passwordEncoder.bCrypt())
//                .and().getOrBuild();

        http
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/v1/api/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/v1/api/jwt").permitAll()
                        .requestMatchers("/**").authenticated()
                )
                .addFilterBefore(new JWTFilter(builderObject, userRepository, jwtUtility), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(customizer -> customizer.authenticationEntryPoint(new Http401UnauthorizedEntryPoint()));

        return http.build();
    }
}
