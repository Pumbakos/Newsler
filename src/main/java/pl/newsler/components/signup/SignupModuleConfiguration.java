package pl.newsler.components.signup;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import pl.newsler.components.user.IUserCrudService;
import pl.newsler.components.user.IUserRepository;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
class SignupModuleConfiguration {
    private final IUserCrudService crudService;
    private final IUserRepository userRepository;
    private final IConfirmationTokenRepository confirmationTokenRepository;
    private final JavaMailSender javaMailSender;

    @Bean(name = "confirmationTokenService")
    ConfirmationTokenService confirmationTokenService() {
        return new ConfirmationTokenService(confirmationTokenRepository);
    }

    @Bean(name = "emailConfirmationService")
    IEmailConfirmationService emailConfirmationService() {
        return new EmailConfirmationService(javaMailSender);
    }

    @Bean(name = "userSignupService")
    IUserSignupService userSignupService(ConfirmationTokenService confirmationTokenService, IEmailConfirmationService emailConfirmationService) {
        return new UserSignupService(confirmationTokenService, emailConfirmationService, userRepository, crudService);
    }
}
