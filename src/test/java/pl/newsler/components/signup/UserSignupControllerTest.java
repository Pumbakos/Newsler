package pl.newsler.components.signup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pl.newsler.api.IUserSignupController;
import pl.newsler.commons.exception.EmailAlreadyConfirmedException;
import pl.newsler.commons.exception.GlobalRestExceptionHandler;
import pl.newsler.commons.exception.InvalidTokenException;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.exception.NLError;
import pl.newsler.commons.exception.NLException;
import pl.newsler.commons.exception.TokenExpiredException;
import pl.newsler.commons.exception.UserAlreadyExistsException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLToken;
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

import static pl.newsler.testcommons.TestUserUtils.email;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.lastName;

public class UserSignupControllerTest {
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
    private final IEmailConfirmationService emailConfirmationService = Mockito.mock(EmailConfirmationService.class); //FIXME: get rid of this mock when pipelines
    private final IUserSignupService service = configuration.userSignupService(configuration.confirmationTokenService(), emailConfirmationService);
    private final IUserSignupController controller = new UserSignupController(service);
    private final GlobalRestExceptionHandler handler = new GlobalRestExceptionHandler();
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
    void shouldReturn200OkAndSignupUserWhenUserDataCorrect() throws NoSuchFieldException, IllegalAccessException {
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

        ResponseEntity<String> response = controller.signup(request);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        Assertions.assertEquals(String.format("Confirmation message will be send to %s", email), response.getBody());
    }

    @Test
    void shouldReturn400BadRequestAndNotSignupUserWhenRequestNull() {
        try {
            controller.signup(null);
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail("Not a desired exception type! Expected: <InvalidUserDataException> but was" + e.getClass().getName());
            }
        }
    }

    @Test
    void shouldReturn400BadRequestAndNotSignupUserWhenUserAlreadyExists() {
        final String email = factory.dotted().getEmail().getValue();
        final UserCreateRequest request = new UserCreateRequest(firstName(), lastName(), email, "Ma47c!n9Pa$$#0rd");

        try {
            controller.signup(request);
        } catch (NLException e) {
            if (e instanceof UserAlreadyExistsException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail("Not a desired exception type! Expected: <UserAlreadyExistsException> but was" + e.getClass().getName());
            }
        }
    }

    @Test
    void shouldReturn400BadRequestAndNotSignupUserWhenUsersEmailInvalid() {
        final UserCreateRequest request = new UserCreateRequest(firstName(), lastName(), "invalid@email", "Ma47c!n9Pa$$#0rd");

        try {
            controller.signup(request);
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail("Not a desired exception type! Expected: <InvalidUserDataException> but was" + e.getClass().getName());
            }
        }
    }

    @Test
    void shouldReturn400BadRequestAndNotSignupUserWhenUsersFirstNameInvalid() {
        final UserCreateRequest request = new UserCreateRequest("", lastName(), email(), "Ma47c!n9Pa$$#0rd");

        try {
            controller.signup(request);
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail("Not a desired exception type! Expected: <InvalidUserDataException> but was" + e.getClass().getName());
            }
        }
    }

    @Test
    void shouldReturn400BadRequestAndNotSignupUserWhenUsersLastNameInvalid() {
        final UserCreateRequest request = new UserCreateRequest(firstName(), "", email(), "Ma47c!n9Pa$$#0rd");

        try {
            controller.signup(request);
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail("Not a desired exception type! Expected: <InvalidUserDataException> but was" + e.getClass().getName());
            }
        }
    }

    @Test
    void shouldReturn400BadRequestAndNotSignupUserWhenUsersPasswordInvalid() {
        final UserCreateRequest request = new UserCreateRequest(firstName(), lastName(), email(), "password");

        try {
            controller.signup(request);
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail("Not a desired exception type! Expected: <InvalidUserDataException> but was" + e.getClass().getName());
            }
        }
    }

    @Test
    void shouldReturn200OkAndConfirmTokenWhenTokenConfirmed() {
        final String token = UUID.randomUUID().toString();
        final NLConfirmationToken confirmationToken = new NLConfirmationToken(NLId.of(random.nextLong()), NLToken.of(token), factory.dotted().map().getId());
        confirmationTokenRepository.save(confirmationToken);

        try {
            controller.confirm(token);
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail("Not a desired exception type! Expected: <InvalidUserDataException> but was" + e.getClass().getName());
            }
        }
    }

    @Test
    void shouldReturn401UnauthorizedAndNotConfirmTokenWhenTokenBlank() {
        try {
            controller.confirm("   ");
        } catch (NLException e) {
            if (e instanceof InvalidTokenException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
            } else {
                Assertions.fail("Not a desired exception type! Expected: <InvalidTokenException> but was" + e.getClass().getName());
            }
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldReturn401UnauthorizedAndNotConfirmTokenWhenTokenEmpty(String token) {
        try {
            controller.confirm(token);
        } catch (NLException e) {
            if (e instanceof InvalidTokenException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
            } else {
                Assertions.fail("Not a desired exception type! Expected: <InvalidTokenException> but was" + e.getClass().getName());
            }
        }
    }

    @Test
    void shouldReturn401UnauthorizedAndNotConfirmTokenWhenTokenInvalid() {
        try {
            controller.confirm("invalid token");
        } catch (NLException e) {
            if (e instanceof InvalidTokenException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
            } else {
                Assertions.fail("Not a desired exception type! Expected: <InvalidTokenException> but was" + e.getClass().getName());
            }
        }
    }

    @Test
    void shouldReturn401UnauthorizedAndNotConfirmTokenWhenTokenAlreadyConfirmed() {
        final String token = UUID.randomUUID().toString();
        final NLConfirmationToken confirmationToken = new NLConfirmationToken(NLId.of(random.nextLong()), NLToken.of(token), factory.dotted().map().getId());
        confirmationTokenRepository.save(confirmationToken);
        confirmationTokenRepository.updateConfirmationDate(confirmationToken.getToken(), LocalDateTime.now());

        try {
            controller.confirm(token);
        } catch (NLException e) {
            if (e instanceof EmailAlreadyConfirmedException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
            } else {
                Assertions.fail("Not a desired exception type! Expected: <EmailAlreadyConfirmedException> but was" + e.getClass().getName());
            }
        }
    }

    @Test
    void shouldReturn401UnauthorizedAndNotConfirmTokenWhenTokenExpired() {
        final String token = UUID.randomUUID().toString();
        final NLConfirmationToken confirmationToken = new NLConfirmationToken(NLId.of(random.nextLong()), NLToken.of(token), factory.dotted().map().getId());
        LocalDateTime now = LocalDateTime.now().minusMinutes(16L);
        LocalDateTime then = now.plusMinutes(1L);
        confirmationToken.setCreationDate(now);
        confirmationToken.setExpirationDate(then);
        confirmationTokenRepository.save(confirmationToken);

        try {
            controller.confirm(token);
        } catch (NLException e) {
            if (e instanceof TokenExpiredException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
            } else {
                Assertions.fail(String.format("Not a desired exception type! Expected: <TokenExpiredException> but was <%s>", e.getClass().getSimpleName()));
            }
        }
    }

    @Test
    void shouldReturn200OkWhenTokenResent() throws NoSuchFieldException, IllegalAccessException {
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

        final String email = factory.dotted().getEmail().getValue();
        final UserResendTokenRequest request = new UserResendTokenRequest(email, factory.dotted_plainPassword());

        ResponseEntity<String> response = controller.resendToken(request);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        Assertions.assertEquals(String.format("Confirmation message will be send to %s", email), response.getBody());
    }

    @Test
    void shouldReturn400BadRequestWhenRequestNull() {
        try {
            controller.resendToken(null);
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail(String.format("Not a desired exception type! Expected: <InvalidUserDataException> but was <%s>", e.getClass().getSimpleName()));
            }
        }
    }

    @Test
    void shouldReturn400BadRequestWhenRequestEmpty() {
        try {
            controller.resendToken(new UserResendTokenRequest("", ""));
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail(String.format("Not a desired exception type! Expected: <InvalidUserDataException> but was <%s>", e.getClass().getSimpleName()));
            }
        }
    }

    @Test
    void shouldReturn400BadRequestWhenRequestBlank() {
        try {
            controller.resendToken(new UserResendTokenRequest("  ", "  "));
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail(String.format("Not a desired exception type! Expected: <InvalidUserDataException> but was <%s>", e.getClass().getSimpleName()));
            }
        }
    }

    @Test
    void shouldReturn400BadRequestWhenEmailInvalid() {
        try {
            controller.resendToken(new UserResendTokenRequest("invalid@email", factory.dashed_plainPassword()));
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail(String.format("Not a desired exception type! Expected: <InvalidUserDataException> but was <%s>", e.getClass().getSimpleName()));
            }
        }
    }

    @Test
    void shouldReturn400BadRequestWhenPasswordInvalid() {
        try {
            controller.resendToken(new UserResendTokenRequest(email(), "invalidPA$$word"));
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail(String.format("Not a desired exception type! Expected: <InvalidUserDataException> but was <%s>", e.getClass().getSimpleName()));
            }
        }
    }

    @Test
    void shouldReturn400BadRequestWhenPasswordDoesNotMatchEmail() {
        try {
            controller.resendToken(new UserResendTokenRequest(factory.dotted().getEmail().getValue(), factory.standard_plainPassword()));
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail(String.format("Not a desired exception type! Expected: <InvalidUserDataException> but was <%s>", e.getClass().getSimpleName()));
            }
        }
    }

    @Test
    void shouldReturn400BadRequestWhenUserNotFound() {
        try {
            controller.resendToken(new UserResendTokenRequest("valid@email.test", factory.standard_plainPassword()));
        } catch (NLException e) {
            if (e instanceof InvalidUserDataException ex) {
                ResponseEntity<NLError> response = handler.handleException(ex);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());
                Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
            } else {
                Assertions.fail(String.format("Not a desired exception type! Expected: <InvalidUserDataException> but was <%s>", e.getClass().getSimpleName()));
            }
        }
    }
}
