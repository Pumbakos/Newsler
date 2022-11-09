package pl.newsler.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.newsler.auth.JWTUtility;
import pl.newsler.components.user.IUserCrudService;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.jwt.JWTFilter;

import javax.annotation.Resource;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfiguration implements WebSecurityCustomizer {
    private final AuthenticationConfiguration authenticationConfiguration;

    @Resource(name = "bCryptPasswordEncoder")
    private final BCryptPasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;

    @Resource(name = "userService")
    private final IUserCrudService userService;
    private final JWTUtility jwtUtility;

//    @Autowired
//    void configure(AuthenticationManagerBuilder builder, DataSource dataSource) throws Exception {
//        builder.jdbcAuthentication()
//                .dataSource(dataSource)
//                .usersByUsernameQuery("SELECT EMAIL, PASSWORD, ENABLED FROM USERS WHERE EMAIL=?")
//                .passwordEncoder(new BCryptPasswordEncoder());
//    }

    @Bean(name = "securityFilterChain")
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .addFilter(new JWTFilter(authenticationConfiguration.getAuthenticationManager(), userRepository, jwtUtility));

        return http.build();
    }

    @Override
    public void customize(WebSecurity web) {
        web.ignoring().antMatchers("/api/jwt"); //FIXME: use .authorizeRequests().antMatchers().permitAll() properly
    }
}
