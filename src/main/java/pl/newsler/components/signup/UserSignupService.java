package pl.newsler.components.signup;

import pl.newsler.api.exceptions.UserDataNotFineException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLToken;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.signup.dto.UserResendTokenRequest;
import pl.newsler.components.user.IUserCrudService;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.ValueProvider;
import pl.newsler.internal.DomainProperties;
import pl.newsler.security.NLIPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

class UserSignupService implements IUserSignupService {
    private final DomainProperties.Schema schema;
    private final String homeDomain;
    private final int port;
    private final ConfirmationTokenService confirmationTokenService;
    private final IEmailConfirmationService emailConfirmationSender;
    private final NLIPasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final IUserCrudService crudService;

    UserSignupService(final DomainProperties properties, final ConfirmationTokenService confirmationTokenService,
                      final IEmailConfirmationService emailConfirmationSender, final NLIPasswordEncoder passwordEncoder,
                      final IUserRepository userRepository, final IUserCrudService crudService) {
        this.schema = properties.getSchema();
        this.homeDomain = properties.getDomainName();
        this.port = properties.getPort();
        this.confirmationTokenService = confirmationTokenService;
        this.emailConfirmationSender = emailConfirmationSender;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.crudService = crudService;
    }

    @Override
    public ValueProvider singUp(UserCreateRequest request) {
        final NLEmail email = NLEmail.of(passwordEncoder.decrypt(request.email()));
        if (!email.validate()) {
            throw new UserDataNotFineException("Email is invalid");
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

        return ValueProvider.REGISTERED;
    }

    @Override
    public ValueProvider confirmToken(NLToken token) {
        final NLConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        if (confirmationToken.getConfirmationDate() != null) {
            throw new IllegalStateException("Email already confirmed");
        }
        final LocalDateTime expirationDate = confirmationToken.getExpirationDate();
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        confirmationTokenService.setConfirmationDate(token);
        enableUser(confirmationToken.getUserId());

        return ValueProvider.CONFIRMED;
    }

    NLConfirmationToken create(NLFirstName name, NLLastName lastName, NLEmail email, NLPassword password) {
        final NLUuid uuid = crudService.create(name, lastName, email, password);
        final NLConfirmationToken token = getConfirmationToken(uuid, generateToken());

        return confirmationTokenService.save(token);
    }

    NLConfirmationToken getConfirmationToken(NLUuid userId, NLToken token) {
        return new NLConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15L),
                userId
        );
    }

    void enableUser(NLUuid uuid) {
        userRepository.enableUser(uuid);
    }

    static NLToken generateToken() {
        return NLToken.of(UUID.randomUUID().toString());
    }

    @Override
    public ValueProvider resendConfirmationToken(UserResendTokenRequest request) {
        final AtomicReference<ValueProvider> value = new AtomicReference<>(ValueProvider.NOT_SENT);
        final NLEmail email = NLEmail.of(passwordEncoder.decrypt(request.email()));
        if (!email.validate()) {
            throw new UserDataNotFineException("Email invalid");
        }

        userRepository.findByEmail(email).ifPresent(u -> {
            if (confirmationTokenService.setTokenExpired(u.map().getId())) {
                final NLToken token = generateToken();
                getConfirmationToken(u.map().getId(), token);
                final String link = createLink(token.getValue());

                emailConfirmationSender.send(
                        u.getEmail().getValue(),
                        emailConfirmationSender.confirmationEmailBuilder(createReceiverName(u.getFirstName(), u.getLastName()), link)
                );

                value.set(ValueProvider.RESENT);
            }
        });

        return value.get();
    }

    private static String createReceiverName(final NLName name, final NLLastName lastName) {
        return String.format("%s %s", name.getValue(), lastName);
    }

    private String createLink(String token) {
        return String.format("%s://%s:%d/v1/auth/sign-up/confirm?token=%s", schema.name().toLowerCase(Locale.ROOT), homeDomain, port, token);
    }
}
