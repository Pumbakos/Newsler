package pl.newsler.components.emaillabs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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

    @Bean(name = "taskExecutor")
    ELATaskExecutor taskExecutor(RestTemplate restTemplate) {
        return new ELATaskExecutor(new ConcurrentLinkedQueue<>(), passwordEncoder, mailRepository, userRepository, restTemplate, new ObjectMapper(), new Gson());
    }

    @Bean(name = "mailService")
    IMailService mailService(ELATaskExecutor taskExecutor) {
        return new MailService(taskExecutor, userRepository, mailRepository);
    }
}
