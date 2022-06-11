package pl.palubiak.dawid.newsler.user.registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.user.model.UserRole;
import pl.palubiak.dawid.newsler.user.registration.emailconfirmation.EmailConfirmationSender;
import pl.palubiak.dawid.newsler.user.registration.requestmodel.ActivationRequest;
import pl.palubiak.dawid.newsler.user.registration.requestmodel.RegistrationRequest;
import pl.palubiak.dawid.newsler.user.registration.token.ConfirmationToken;
import pl.palubiak.dawid.newsler.user.registration.token.ConfirmationTokenService;
import pl.palubiak.dawid.newsler.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
public class UserRegistrationService {
    private final EmailValidator emailValidator;
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailConfirmationSender emailConfirmationSender;

    public ValueProvider register(RegistrationRequest request) {
        boolean isEmailValid = emailValidator.test(request.email());
        if (!isEmailValid) {
            throw new IllegalArgumentException("Email is not valid");
        }

        String token = userService.singUp(
                new User(
                        request.name(),
                        request.lastName(),
                        request.email(),
                        request.password(),
                        UserRole.USER
                )
        );

        String link = ValueProvider.HOME_DOMAIN.getValue() + "/api/users/registration/confirm?token=" + token;
        emailConfirmationSender.send(
                request.email(),
                emailConfirmationSender.confirmationEmailBuilder(request.name(), link)
        );

        return ValueProvider.REGISTERED;
    }

    public ValueProvider resendConfirmationToken(ActivationRequest request) {
        AtomicReference<ValueProvider> valueProvider = new AtomicReference<>(ValueProvider.NOT_SENT);

        boolean isEmailValid = emailValidator.test(request.getEmail());
        if (!isEmailValid) {
            throw new IllegalArgumentException("Email is not valid");
        }

        Optional<User> optionalUser = userService.getUserByEmailAndPassword(request.getEmail(), request.getPassword());
        optionalUser.ifPresent(user -> {
            if(confirmationTokenService.setTokenExpired(user)){
                String token = UserService.generateToken();
                userService.getConfirmationToken(user, token);
                String link = ValueProvider.HOME_DOMAIN.getValue() + "/api/users/registration/confirm?token=" + token;

                emailConfirmationSender.send(request.getEmail(),
                        emailConfirmationSender
                                .confirmationEmailBuilder(user.getName(), link));

                valueProvider.set(ValueProvider.RESENT);
            }
        });

        return valueProvider.get();
    }

    @Transactional
    public ValueProvider confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        if (confirmationToken.getConfirmationDate() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expirationDate = confirmationToken.getExpirationDate();

        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        confirmationTokenService.setConfirmationDate(token);
        userService.enableUser(confirmationToken.getUser().getEmail());

        return ValueProvider.CONFIRMED;
    }
}
