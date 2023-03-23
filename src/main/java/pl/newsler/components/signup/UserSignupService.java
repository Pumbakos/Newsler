package pl.newsler.components.signup;

import lombok.RequiredArgsConstructor;
        import org.jetbrains.annotations.NotNull;
        import org.springframework.beans.factory.annotation.Value;
        import pl.newsler.commons.exception.EmailAlreadyConfirmedException;
        import pl.newsler.commons.exception.InvalidTokenException;
        import pl.newsler.commons.exception.InvalidUserDataException;
        import pl.newsler.commons.exception.TokenExpiredException;
        import pl.newsler.components.signup.exception.UserAlreadyExistsException;
        import pl.newsler.commons.model.NLEmail;
        import pl.newsler.commons.model.NLFirstName;
        import pl.newsler.commons.model.NLId;
        import pl.newsler.commons.model.NLLastName;
        import pl.newsler.commons.model.NLName;
        import pl.newsler.commons.model.NLPassword;
        import pl.newsler.commons.model.NLStringValue;
        import pl.newsler.commons.model.NLToken;
        import pl.newsler.commons.model.NLUuid;
        import pl.newsler.components.signup.usecase.UserCreateRequest;
        import pl.newsler.components.signup.usecase.UserResendTokenRequest;
        import pl.newsler.components.user.IUserCrudService;
        import pl.newsler.components.user.IUserRepository;
        import pl.newsler.internal.NewslerServiceProperties;
        import pl.newsler.security.NLIPasswordEncoder;

        import java.time.LocalDateTime;
        import java.util.Locale;
        import java.util.Random;
        import java.util.UUID;

@RequiredArgsConstructor
class UserSignupService implements IUserSignupService {
    private final ConfirmationTokenService confirmationTokenService;
    private final IEmailConfirmationService emailConfirmationSender;
    private final NLIPasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final IUserCrudService crudService;
    private final Random random;
    @Value("${newsler.service.schema}")
    private NewslerServiceProperties.Schema schema;
    @Value("${newsler.service.domain-name}")
    private String homeDomain;
    @Value("${newsler.service.port}")
    private int port;

    @Override
    public @NotNull NLStringValue singUp(UserCreateRequest request) throws InvalidUserDataException, UserAlreadyExistsException {
        final NLEmail email = NLEmail.of(request.email());
        final NLFirstName firstName = NLFirstName.of(request.name());
        final NLPassword password = NLPassword.of(request.password());
        final NLLastName lastName = NLLastName.of(request.lastName());
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
        enableUser(confirmationToken.getUserUuid());

        return NLStringValue.of("Email confirmed");
    }

    @Override
    public @NotNull NLStringValue resendConfirmationToken(@NotNull UserResendTokenRequest request) throws InvalidUserDataException {
        final String errorMessage = "Either email or password is incorrect";
        final NLEmail email = NLEmail.of(request.email());
        final NLPassword password = NLPassword.of(request.password());
        if (!email.validate() || !password.validate()) {
            throw new InvalidUserDataException(errorMessage);
        }

        userRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    if (!passwordEncoder.bCrypt().matches(request.password(), user.getPassword())) {
                        throw new InvalidUserDataException(errorMessage);
                    }

                    if (confirmationTokenService.setTokenExpired(user.map().getId())) {
                        final NLToken token = generateToken();
                        createConfirmationToken(user.map().getId(), token);
                        final String link = createLink(token.getValue());

                        emailConfirmationSender.send(
                                user.getEmail().getValue(),
                                emailConfirmationSender.confirmationEmailBuilder(createReceiverName(user.getFirstName(), user.getLastName()), link)
                        );
                    } else {
                        throw new InvalidUserDataException("Could not confirm token");
                    }
                },
                () -> {
                    throw new InvalidUserDataException(errorMessage);
                }
        );

        return NLStringValue.of(String.format("Confirmation message will be send to %s", email.getValue()));
    }

    private static NLToken generateToken() {
        return NLToken.of(UUID.randomUUID().toString());
    }

    private NLConfirmationToken create(NLFirstName name, NLLastName lastName, NLEmail email, NLPassword password) {
        final NLUuid uuid = crudService.create(name, lastName, email, password);
        final NLConfirmationToken token = createConfirmationToken(uuid, generateToken());
        return confirmationTokenService.save(token);
    }

    private NLConfirmationToken createConfirmationToken(NLUuid userId, NLToken token) {
        return new NLConfirmationToken(
                NLId.of(random.nextLong()),
                token,
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