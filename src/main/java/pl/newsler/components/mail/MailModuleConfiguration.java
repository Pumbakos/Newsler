package pl.newsler.components.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.NLIPasswordEncoder;


@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
class MailModuleConfiguration {
    private final IUserRepository userRepository;
    private final IMailRepository mailRepository;
    private final NLIPasswordEncoder passwordEncoder;

    @Bean(name = "mailService")
    MailService mailService() {
        return new MailService(new ConcurrentTaskExecutor(), userRepository, mailRepository, passwordEncoder);
    }
}
