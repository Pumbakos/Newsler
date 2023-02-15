package pl.newsler.components.receiver;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.newsler.components.user.IUserRepository;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class ReceiverModuleConfiguration {
    private final IReceiverRepository receiverRepository;
    private final IUserRepository userRepository;

    @Bean(name = "receiverService")
    IReceiverService receiverService() {
        return new ReceiverService(receiverRepository, userRepository);
    }
}
