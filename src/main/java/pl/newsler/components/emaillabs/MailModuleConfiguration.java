package pl.newsler.components.emaillabs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.NLIPasswordEncoder;

import java.util.concurrent.ConcurrentLinkedQueue;

@ComponentScan
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
class MailModuleConfiguration {
    private final IUserRepository userRepository;
    private final IMailRepository mailRepository;
    private final NLIPasswordEncoder passwordEncoder;

    @Bean(name = "restTemplate")
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = "elaTaskExecutor")
    IELATaskExecutor taskExecutor(RestTemplate restTemplate) {
        return new ELATaskExecutor(new ConcurrentLinkedQueue<>(), passwordEncoder, mailRepository, userRepository, restTemplate, new ObjectMapper());
    }

    @Bean(name = "mailService")
    IELAMailService mailService(IELATaskExecutor taskExecutor) {
        return new ELAMailService(taskExecutor, userRepository, mailRepository);
    }
}
