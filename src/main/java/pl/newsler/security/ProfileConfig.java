package pl.newsler.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;

@Configuration
public class ProfileConfig extends AbstractEnvironment {
    @Bean
    public void setActiveProfiles() {
        super.setActiveProfiles("dev");
    }
}
