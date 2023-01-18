package pl.newsler.components.signup;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import pl.newsler.commons.exception.EmailAlreadyConfirmedException;
import pl.newsler.commons.exception.InvalidTokenException;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.exception.TokenExpiredException;
import pl.newsler.commons.exception.UserAlreadyExistsException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLStringValue;
import pl.newsler.commons.models.NLToken;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.signup.dto.UserResendTokenRequest;
import pl.newsler.components.user.IUserCrudService;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.internal.DomainProperties;
import pl.newsler.security.NLIPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
class UserSignupService implements IUserSignupService {
    private final ConfirmationTokenService confirmationTokenService;
    private final IEmailConfirmationService emailConfirmationSender;
    private final NLIPasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final IUserCrudService crudService;
    @Value("${newsler.schema}")
    private DomainProperties.Schema schema;
    @Value("${newsler.domain-name}")
    private String homeDomain;
    @Value("${newsler.port}")
    private int port;

    @Override
    public @NotNull NLStringValue singUp(UserCreateRequest request) throws InvalidUserDataException, UserAlreadyExistsException {
        final NLEmail email = NLEmail.of(passwordEncoder.decrypt(request.email()));
        if (!email.validate()) {
            throw new InvalidUserDataException("Email is invalid");
        }

        final NLFirstName firstName = NLFirstName.of(passwordEncoder.decrypt(request.name()));
        final NLPassword password = NLPassword.of(passwordEncoder.decrypt(request.password()));
        final NLLastName lastName = NLLastName.of(passwordEncoder.decrypt(request.lastName()));
        final NLConfirmationToken token = create(firstName, lastName, email, password);
        final String link = createLink(token.getToken().getValue());

        emailConfirmationSender.send(
                email.getValue(),
                emailConfirmationSender.confirmationEmailBuilder(createReceiverName(firstName, lastName), link)
        );

        return NLStringValue.of(String.format("Confirmation message will be send to %s", email.getValue()));
    }

    @Override
    public @NotNull NLStringValue confirmToken(NLToken token) throws InvalidTokenException, EmailAlreadyConfirmedException, TokenExpiredException {
        if (!token.validate()) {
            throw new InvalidTokenException();
        }

        final NLConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token not found"));

        if (confirmationToken.getConfirmationDate() != null) {
            throw new EmailAlreadyConfirmedException("Email already confirmed");
        }

        final LocalDateTime expirationDate = confirmationToken.getExpirationDate();
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expired");
        }

        confirmationTokenService.setConfirmationDate(token);
        enableUser(confirmationToken.getUserId());

        return NLStringValue.of("Email confirmed");
    }

    @Override
    public @NotNull NLStringValue resendConfirmationToken(@NotNull UserResendTokenRequest request) throws InvalidUserDataException {
        final NLEmail email = NLEmail.of(passwordEncoder.decrypt(request.email()));
        if (!email.validate()) {
            throw new InvalidUserDataException("Email invalid");
        }

        userRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    if (confirmationTokenService.setTokenExpired(user.map().getId())) {
                        final NLToken token = generateToken();
                        getConfirmationToken(user.map().getId(), token);
                        final String link = createLink(token.getValue());

                        emailConfirmationSender.send(
                                user.getEmail().getValue(),
                                emailConfirmationSender.confirmationEmailBuilder(createReceiverName(user.getFirstName(), user.getLastName()), link)
                        );
                    }
                },
                () -> {
                    throw new InvalidUserDataException();
                }
        );

        return NLStringValue.of(String.format("Confirmation message will be send to %s", email.getValue()));
    }

    private static NLToken generateToken() {
        return NLToken.of(UUID.randomUUID().toString());
    }

    private NLConfirmationToken create(NLFirstName name, NLLastName lastName, NLEmail email, NLPassword password) {
        final NLUuid uuid = crudService.create(name, lastName, email, password);
        final NLConfirmationToken token = getConfirmationToken(uuid, generateToken());
        return confirmationTokenService.save(token);
    }

    private NLConfirmationToken getConfirmationToken(NLUuid userId, NLToken token) {
        return new NLConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15L),
                userId
        );
    }

    private void enableUser(NLUuid uuid) {
        userRepository.enableUser(uuid);
    }


    private String createReceiverName(final NLName name, final NLLastName lastName) {
        return String.format("%s %s", name.getValue(), lastName);
    }

    private String createLink(String token) {
        return String.format("%s://%s:%d/v1/api/auth/sign-up/confirm?token=%s", schema.getName().toLowerCase(Locale.ROOT), homeDomain, port, token);
    }
}