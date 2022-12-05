package pl.newsler.components.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.newsler.security.NLIPasswordEncoder;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
class UserConfiguration {
    private final IUserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;

    @Bean(name = "userService")
    IUserCrudService userService(){
        return new UserCrudService(userRepository, passwordEncoder);
    }
}
