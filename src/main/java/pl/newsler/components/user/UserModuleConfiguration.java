package pl.newsler.components.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pl.newsler.security.NLIPasswordEncoder;

@ComponentScan
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
class UserModuleConfiguration {
    private final IUserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;

    @Bean(name = "userService")
    IUserCrudService userService() {
        return new UserCrudService(userRepository, passwordEncoder);
    }
}
