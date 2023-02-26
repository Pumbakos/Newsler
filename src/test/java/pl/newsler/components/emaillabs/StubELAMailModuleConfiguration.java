package pl.newsler.components.emaillabs;

import org.springframework.web.client.RestTemplate;
import pl.newsler.components.emaillabs.executor.ELARequestBuilder;
import pl.newsler.components.emaillabs.executor.IELATaskInstantExecutor;
import pl.newsler.components.emaillabs.executor.IELATaskScheduledExecutor;
import pl.newsler.components.receiver.IReceiverService;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.NLIPasswordEncoder;


public class StubELAMailModuleConfiguration {
    private final ELAMailModuleConfiguration configuration;

    public StubELAMailModuleConfiguration(IUserRepository userRepository, IELAMailRepository mailRepository, NLIPasswordEncoder passwordEncoder, IReceiverService receiverService) {
        this.configuration = new ELAMailModuleConfiguration(userRepository, mailRepository, passwordEncoder, receiverService);
    }

    public ELARequestBuilder elaParamBuilder() {
        return configuration.elaParamBuilder();
    }

    public IELATaskInstantExecutor taskInstantExecutor(RestTemplate restTemplate, ELARequestBuilder requestBuilder) {
        return configuration.taskInstantExecutor(restTemplate, requestBuilder);
    }

    public IELATaskScheduledExecutor taskScheduledExecutor(RestTemplate restTemplate, ELARequestBuilder requestBuilder) {
        return configuration.taskScheduledExecutor(restTemplate, requestBuilder);
    }

    public IELAMailService mailService(IELATaskInstantExecutor taskInstantExecutor, IELATaskScheduledExecutor taskScheduledExecutor) {
        return configuration.mailService(taskInstantExecutor, taskScheduledExecutor);
    }

    public IELATemplateService templateService(ELARequestBuilder requestBuilder, RestTemplate restTemplate) {
        return configuration.templateService(requestBuilder, restTemplate);
    }
}
