package pl.newsler.components.signup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pl.newsler.commons.exception.EmailAlreadyConfirmedException;
import pl.newsler.commons.exception.InvalidTokenException;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.exception.TokenExpiredException;
import pl.newsler.commons.exception.UserAlreadyExistsException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLStringValue;
import pl.newsler.commons.models.NLToken;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.signup.dto.UserResendTokenRequest;
import pl.newsler.components.user.IUserCrudService;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.StubUserModuleConfiguration;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.internal.DomainProperties;
import pl.newsler.security.NLIPasswordEncoder;
import pl.newsler.security.StubNLPasswordEncoder;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static pl.newsler.testcommons.TestUserUtils.email;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.lastName;

@RunWith(MockitoJUnitRunner.class)
class UserSignupServiceTest {
    private final StubConfirmationTokenRepository confirmationTokenRepository = new StubConfirmationTokenRepository();
    private final NLIPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final IUserRepository userRepository = new StubUserRepository();
    private final StubUserModuleConfiguration userModuleConfiguration = new StubUserModuleConfiguration(userRepository, passwordEncoder);
    private final IUserCrudService crudService = userModuleConfiguration.userService();
    private final JavaMailSender mailSender = new JavaMailSenderImpl();
    private final SignupModuleConfiguration configuration = new SignupModuleConfiguration(
            confirmationTokenRepository,
            passwordEncoder,
            userRepository,
            mailSender,
            crudService
    );
    private final IEmailConfirmationService emailConfirmationService = Mockito.mock(EmailConfirmationService.class);
    private final IUserSignupService service = configuration.userSignupService(configuration.confirmationTokenService(), emailConfirmationService);
    private final TestUserFactory factory = new TestUserFactory();
    private final Random random = new SecureRandom();

    @BeforeEach
    void beforeEach() {
        factory.standard().setId(
                crudService.create(
                        NLFirstName.of(factory.standard().getFirstName().getValue()),
                        NLLastName.of(factory.standard().getLastName().getValue()),
                        NLEmail.of(factory.standard().getEmail().getValue()),
                        NLPassword.of(factory.standard().getNLPassword().getValue())
                ));
        factory.dashed().setId(
                crudService.create(
                        NLFirstName.of(factory.dashed().getFirstName().getValue()),
                        NLLastName.of(factory.dashed().getLastName().getValue()),
                        NLEmail.of(factory.dashed().getEmail().getValue()),
                        NLPassword.of(factory.dashed().getNLPassword().getValue())
                ));
        factory.dotted().setId(
                crudService.create(
                        NLFirstName.of(factory.dotted().getFirstName().getValue()),
                        NLLastName.of(factory.dotted().getLastName().getValue()),
                        NLEmail.of(factory.dotted().getEmail().getValue()),
                        NLPassword.of(factory.dotted().getNLPassword().getValue())
                ));

        final String standardToken = UUID.randomUUID().toString();
        final String dashedToken = UUID.randomUUID().toString();
        final String dottedToken = UUID.randomUUID().toString();
        confirmationTokenRepository.save(new NLConfirmationToken(NLId.of(random.nextLong()), NLToken.of(standardToken), factory.standard().map().getId()));
        confirmationTokenRepository.save(new NLConfirmationToken(NLId.of(random.nextLong()), NLToken.of(dashedToken), factory.dashed().map().getId()));
        confirmationTokenRepository.save(new NLConfirmationToken(NLId.of(random.nextLong()), NLToken.of(dottedToken), factory.dotted().map().getId()));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSignupUserWhenValidData() throws NoSuchFieldException, IllegalAccessException {
        final Field schema = service.getClass().getDeclaredField("schema");
        final Field homeDomain = service.getClass().getDeclaredField("homeDomain");
        final Field port = service.getClass().getDeclaredField("port");

        schema.setAccessible(true);
        schema.set(service, DomainProperties.Schema.HTTP);
        homeDomain.setAccessible(true);
        homeDomain.set(service, "localhost");
        port.setAccessible(true);
        port.set(service, 8080);

        Mockito.doNothing().when(emailConfirmationService).send(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        final String email = email();
        final UserCreateRequest request = new UserCreateRequest(firstName(), lastName(), email, "Ma47c!n9Pa$$#0rd");

        AtomicReference<NLStringValue> value = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> value.set(service.singUp(request)));

        NLStringValue stringValue = value.get();
        Assertions.assertNotNull(stringValue);
        Assertions.assertEquals(NLStringValue.of(String.format("Confirmation message will be send to %s", email)), stringValue);
    }

    @Test
    void shouldNotSignupUserAndThrowUserAlreadyExistsExceptionWhenUserWithGivenEmailExists() {
        final String email = factory.dashed().getEmail().getValue();
        final UserCreateRequest request = new UserCreateRequest(firstName(), lastName(), email, "Ma47c!n9Pa$$#0rd");

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> service.singUp(request));
    }

    @Test
    void shouldNotSignupUserAndThrowInvalidUserDataExceptionWhenAnyDataInvalid() {
        final UserCreateRequest invalidEmailRequest = new UserCreateRequest(firstName(), lastName(), "invalid@Email", "Ma47c!n9Pa$$#0rd");
        final UserCreateRequest invalidPasswordRequest = new UserCreateRequest(firstName(), lastName(), "valid@email.test", "InvalidPassword!!!");

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.singUp(invalidEmailRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.singUp(invalidPasswordRequest));
    }

    @Test
    void shouldConfirmTokenWhenTokenValid(){
        final NLUuid uuid = factory.dashed().map().getId();
        final NLToken token = NLToken.of(UUID.randomUUID().toString());
        final NLConfirmationToken confirmationToken = new NLConfirmationToken(NLId.of(random.nextLong()), token, uuid);
        confirmationTokenRepository.save(confirmationToken);

        Assertions.assertDoesNotThrow(() -> service.confirmToken(token));
    }

    @Test
    void shouldNotConfirmTokenAndThrowInvalidTokenExceptionWhenTokenNotFound(){
        final NLToken invalidToken = NLToken.of(UUID.randomUUID().toString());
        Assertions.assertThrows(InvalidTokenException.class, () -> service.confirmToken(invalidToken));
    }

    @Test
    void shouldNotConfirmTokenAndThrowInvalidTokenExceptionWhenTokenInvalid(){
        final NLToken invalidToken = NLToken.of("INVALID_TOKEN");
        Assertions.assertThrows(InvalidTokenException.class, () -> service.confirmToken(invalidToken));
    }

    @Test
    void shouldNotConfirmTokenAndThrowEmailAlreadyConfirmedExceptionWhenTokenAlreadyConfirmed(){
        final NLConfirmationToken confirmationToken = confirmationTokenRepository.findAll().get(0);
        final NLToken token = confirmationToken.getToken();
        confirmationTokenRepository.updateConfirmationDate(token, LocalDateTime.now().minusMinutes(13));

        Assertions.assertThrows(EmailAlreadyConfirmedException.class, () -> service.confirmToken(token));
    }

    @Test
    void shouldNotConfirmTokenAndThrowTokenExpiredExceptionWhenTokenExpired(){
        final NLUuid uuid = factory.dashed().map().getId();
        final LocalDateTime now = LocalDateTime.now().minusMinutes(16L);
        final LocalDateTime then = now.plusMinutes(1L);
        final NLToken token = NLToken.of(UUID.randomUUID().toString());

        final NLConfirmationToken confirmationToken = new NLConfirmationToken();
        confirmationToken.setUserId(uuid);
        confirmationToken.setId(NLId.of(725414413L));
        confirmationToken.setCreationDate(now);
        confirmationToken.setExpirationDate(then);
        confirmationToken.setToken(token);

        confirmationTokenRepository.save(confirmationToken);

        Assertions.assertThrows(TokenExpiredException.class, () -> service.confirmToken(token));
    }

    @Test
    void shouldResendConfirmationTokenWhenValidData() throws NoSuchFieldException, IllegalAccessException {
        final Field schema = service.getClass().getDeclaredField("schema");
        final Field homeDomain = service.getClass().getDeclaredField("homeDomain");
        final Field port = service.getClass().getDeclaredField("port");

        final String email = factory.dashed().getEmail().getValue();
        final String password = factory.dashed_plainPassword();
        final UserResendTokenRequest request = new UserResendTokenRequest(email, password);

        schema.setAccessible(true);
        schema.set(service, DomainProperties.Schema.HTTP);
        homeDomain.setAccessible(true);
        homeDomain.set(service, "localhost");
        port.setAccessible(true);
        port.set(service, 8080);

        Mockito.doNothing().when(emailConfirmationService).send(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Assertions.assertDoesNotThrow(() -> service.resendConfirmationToken(request));
    }

    @Test
    void shouldNotResendConfirmationTokenAndThrowInvalidUserDataExceptionWhenInvalidEmail(){
        final String validEmail = factory.dashed().getEmail().getValue();
        final String invalidEmail = "invalid@email";
        final String invalidPassword = "!NValidPassword";
        final UserResendTokenRequest invalidPasswordRequest = new UserResendTokenRequest(validEmail, invalidPassword);
        final UserResendTokenRequest invalidEmailRequest = new UserResendTokenRequest(invalidEmail, invalidPassword);

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.resendConfirmationToken(invalidPasswordRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.resendConfirmationToken(invalidEmailRequest));
    }

    @Test
    void shouldNotResendConfirmationTokenAndThrowInvalidUserDataExceptionWhenEmailDoesNotMatchPassword(){
        final String validEmail = factory.dashed().getEmail().getValue();
        final String invalidPassword = "Ma7Tch!ngP4$$wor3";
        final UserResendTokenRequest request = new UserResendTokenRequest(validEmail, invalidPassword);

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.resendConfirmationToken(request));
    }

    @Test
    void shouldNotResendConfirmationTokenAndThrowInvalidUserDataExceptionWhenUserNotFound(){
        final UserResendTokenRequest request = new UserResendTokenRequest("valid@email.test", "Ma77ching94$$wor3");

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.resendConfirmationToken(request));
    }
}