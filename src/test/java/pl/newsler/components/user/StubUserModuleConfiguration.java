package pl.newsler.components.user;

import pl.newsler.components.emaillabs.IELATemplateService;
import pl.newsler.security.NLIPasswordEncoder;

public class StubUserModuleConfiguration {
    private final UserModuleConfiguration configuration;

    public StubUserModuleConfiguration(final IUserRepository repository, final NLIPasswordEncoder passwordEncoder, IELATemplateService templateService) {
        configuration = new UserModuleConfiguration(repository, passwordEncoder, templateService);
    }

    public IUserCrudService userService() {
        return configuration.userService();
    }
}
