package pl.newsler.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import pl.newsler.auth.JWTUtility;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.IUserService;
import pl.newsler.security.NLIPasswordEncoder;
import pl.newsler.security.jwt.JWTFilter;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebSecurityCustomizer {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final IUserService userService;
    private final IUserRepository userRepository;
    private final JWTUtility jwtUtility;
    private final NLIPasswordEncoder passwordEncoder;

    @Autowired
    @SuppressWarnings({"java:S5344"})
        //provided in implementation class
    void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userService)
                .passwordEncoder(passwordEncoder.bCrypt());
    }

    @Bean(name = "SecurityFilterChain")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers("/test2").authenticated()
                .antMatchers("/test3").hasRole("ADMIN")
                .and()
                .addFilter(new JWTFilter(authenticationConfiguration.getAuthenticationManager(), userRepository, jwtUtility));

        return http.build();
    }

    @Override
    public void customize(WebSecurity web) {
        web.ignoring().antMatchers("/api/jwt"); //FIXME: use .authorizeRequests().antMatchers().permitAll() properly
    }
}
