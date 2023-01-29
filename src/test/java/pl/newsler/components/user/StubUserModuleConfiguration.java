package pl.newsler.components.user;

import pl.newsler.security.NLIPasswordEncoder;

public class StubUserModuleConfiguration {
    private final UserModuleConfiguration configuration;

    public StubUserModuleConfiguration(final IUserRepository repository, final NLIPasswordEncoder passwordEncoder) {
        configuration = new UserModuleConfiguration(repository, passwordEncoder);
    }

    public IUserCrudService userService() {
        return configuration.userService();
    }
}
