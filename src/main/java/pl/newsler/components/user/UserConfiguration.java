package pl.newsler.components.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.newsler.security.NLIPasswordEncoder;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
class UserConfiguration {
    private final IUserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public IUserService userService(){
        return new UserService(userRepository, passwordEncoder, bCryptPasswordEncoder);
    }
}
