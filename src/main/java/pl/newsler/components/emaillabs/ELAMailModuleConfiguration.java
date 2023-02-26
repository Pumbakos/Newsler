package pl.newsler.components.emaillabs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.client.RestTemplate;
import pl.newsler.components.emaillabs.executor.ELAInstantMailDetails;
import pl.newsler.components.emaillabs.executor.ELAParamBuilder;
import pl.newsler.components.emaillabs.executor.ELAScheduleMailDetails;
import pl.newsler.components.emaillabs.executor.IELATaskInstantExecutor;
import pl.newsler.components.emaillabs.executor.IELATaskScheduledExecutor;
import pl.newsler.components.receiver.IReceiverService;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.NLIPasswordEncoder;

import java.util.concurrent.ConcurrentLinkedQueue;

@ComponentScan
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
class ELAMailModuleConfiguration {
    private final IUserRepository userRepository;
    private final IELAMailRepository mailRepository;
    private final NLIPasswordEncoder passwordEncoder;
    private final IReceiverService receiverService;

    @Bean(name = "restTemplate")
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = "globalObjectMapper")
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean(name = "elaParamBuilder")
    ELAParamBuilder elaParamBuilder() {
        return new ELAParamBuilder();
    }

    @Bean(name = "elaTaskInstantExecutor")
    IELATaskInstantExecutor taskInstantExecutor(RestTemplate restTemplate, ELAParamBuilder paramBuilder) {
        return new ELATaskInstantExecutor(
                new ConcurrentLinkedQueue<>(),
                new ConcurrentTaskExecutor(),
                passwordEncoder,
                mailRepository,
                receiverService,
                userRepository,
                restTemplate,
                paramBuilder
        );
    }

    @Bean(name = "elaTaskScheduledExecutor")
    IELATaskScheduledExecutor taskScheduledExecutor(RestTemplate restTemplate, ELAParamBuilder paramBuilder) {
        return new ELATaskScheduledExecutor(
                new ConcurrentLinkedQueue<>(),
                new ConcurrentTaskScheduler(),
                passwordEncoder,
                mailRepository,
                receiverService,
                userRepository,
                restTemplate,
                paramBuilder
        );
    }

    @Bean(name = "mailService")
    IELAMailService mailService(IELATaskInstantExecutor taskInstantExecutor, IELATaskScheduledExecutor taskScheduledExecutor) {
        return new ELAMailService(taskInstantExecutor, taskScheduledExecutor, userRepository, mailRepository);
    }
}
