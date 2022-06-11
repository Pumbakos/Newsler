package pl.palubiak.dawid.newsler.config.security.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.palubiak.dawid.newsler.user.service.UserService;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                    .authorizeRequests()
                        .antMatchers(HttpMethod.GET, "/users/*")
                            .hasAuthority("SCOPE_read")
                        .antMatchers(HttpMethod.POST, "/users/*")
                            .hasAuthority("SCOPE_write")
                            .anyRequest()
                                .authenticated()
                    .and()
                        .oauth2ResourceServer()
                            .jwt();
//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/api/**")
//                .permitAll()
//                .anyRequest()
//                .authenticated().and()
//                .oauth2Login();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(this.passwordEncoder);
        provider.setUserDetailsService(this.userService);

        return provider;
    }
}
