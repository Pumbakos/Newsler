package pl.newsler.components.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.newsler.components.receiver.IReceiverRepository;
import pl.newsler.components.user.IUserRepository;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class SubscriptionModuleConfiguration {
    private final IReceiverRepository receiverRepository;
    private final IUserRepository userRepository;

    @Bean(name = "subscriptionService")
    ISubscriptionService subscriptionService() {
        return new SubscriptionService(receiverRepository, userRepository);
    }
}
