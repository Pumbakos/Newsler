package pl.newsler.components.signup;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.StubUserModuleConfiguration;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.security.NLIPasswordEncoder;
import pl.newsler.security.StubNLPasswordEncoder;

class UserSignupServiceTest {
    private final StubConfirmationTokenRepository confirmationTokenRepository = new StubConfirmationTokenRepository();
    private final NLIPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final IUserRepository userRepository = new StubUserRepository();
    private final StubUserModuleConfiguration userModuleConfiguration = new StubUserModuleConfiguration(userRepository, passwordEncoder);
    private final SignupModuleConfiguration configuration = new SignupModuleConfiguration(
            userModuleConfiguration.userService(),
            userRepository,
            confirmationTokenRepository,
            new JavaMailSenderImpl()
    );
    private final IUserSignupService service = new UserSignupService(
            configuration.confirmationTokenService(),
            configuration.emailConfirmationService(),
            userRepository,
            userModuleConfiguration.userService()
    );

    @Test
    void shouldSignupUserWhenValidData() {

    }
}