package pl.newsler.components.receiver;

import pl.newsler.components.user.IUserRepository;

public class StubReceiverModuleConfiguration {
    private final ReceiverModuleConfiguration configuration;
    public StubReceiverModuleConfiguration(IReceiverRepository receiverRepository, IUserRepository userRepository) {
        configuration = new ReceiverModuleConfiguration(receiverRepository, userRepository);
    }

    public IReceiverService receiverService() {
        return configuration.receiverService();
    }
}
