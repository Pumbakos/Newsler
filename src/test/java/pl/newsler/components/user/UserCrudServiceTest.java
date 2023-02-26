package pl.newsler.components.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.components.signup.exception.UserAlreadyExistsException;
import pl.newsler.commons.exception.ValidationException;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.user.usecase.UserDeleteRequest;
import pl.newsler.components.user.usecase.UserGetRequest;
import pl.newsler.components.user.usecase.UserGetResponse;
import pl.newsler.components.user.usecase.UserUpdateRequest;
import pl.newsler.security.StubNLPasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static pl.newsler.testcommons.TestUserUtils.email;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.lastName;
import static pl.newsler.testcommons.TestUserUtils.secretOrAppKey;
import static pl.newsler.testcommons.TestUserUtils.smtpAccount;

class UserCrudServiceTest {
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubUserRepository userRepository = new StubUserRepository();
    private final UserModuleConfiguration configuration = new UserModuleConfiguration(
            userRepository,
            passwordEncoder
    );
    private final IUserCrudService service = configuration.userService();
    private final TestUserFactory factory = new TestUserFactory();

    @BeforeEach
    void beforeEach() {
        factory.standard().setId(
                service.create(
                        NLFirstName.of(factory.standard().getFirstName().getValue()),
                        NLLastName.of(factory.standard().getLastName().getValue()),
                        NLEmail.of(factory.standard().getEmail().getValue()),
                        NLPassword.of(factory.standard().getNLPassword().getValue())
                ));
        factory.dashed().setId(
                service.create(
                        NLFirstName.of(factory.dashed().getFirstName().getValue()),
                        NLLastName.of(factory.dashed().getLastName().getValue()),
                        NLEmail.of(factory.dashed().getEmail().getValue()),
                        NLPassword.of(factory.dashed().getNLPassword().getValue())
                ));
        factory.dotted().setId(
                service.create(
                        NLFirstName.of(factory.dotted().getFirstName().getValue()),
                        NLLastName.of(factory.dotted().getLastName().getValue()),
                        NLEmail.of(factory.dotted().getEmail().getValue()),
                        NLPassword.of(factory.dotted().getNLPassword().getValue())
                ));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void shouldGetUserWhenValidData() {
        final String emailStandard = factory.standard().getEmail().getValue();
        final UserGetRequest request = new UserGetRequest(emailStandard, factory.standard().getPassword());
        UserGetResponse response = service.get(request);

        Assertions.assertNotNull(response);
        Assertions.assertDoesNotThrow(() -> service.get(request));
    }

    @Test
    void shouldNotGetUserAndThrowInvalidUserDataExceptionWhenPasswordDoesNotMatchUsersPassword() {
        final UserGetRequest blankRequest = new UserGetRequest(factory.dashed().getEmail().getValue(), "Ma77ching94$$wor3");

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.get(blankRequest));
    }

    @Test
    void shouldNotGetUserAndThrowInvalidUserDataExceptionWhenBlankData() {
        final UserGetRequest nullRequest = new UserGetRequest(null, null);
        final UserGetRequest blankRequest = new UserGetRequest(" ", "");

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.get(null));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.get(nullRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.get(blankRequest));
    }

    @Test
    void shouldNotGetUserAndThrowInvalidUserDataExceptionWhenDataInvalid() {
        final UserGetRequest invalidDataRequest = new UserGetRequest("invalid@email", "invalid$password");
        final UserGetRequest invalidPasswordRequest = new UserGetRequest("valid@email.in", "invalid$password");
        final UserGetRequest notFoundUserRequest = new UserGetRequest(email(), "%**P26AZs>^#!Gp<");

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.get(invalidDataRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.get(invalidPasswordRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.get(notFoundUserRequest));
    }

    @Test
    void shouldCreateNewUserWhenValidData() {
        final AtomicReference<NLUuid> first = new AtomicReference<>();
        final AtomicReference<NLUuid> second = new AtomicReference<>();

        Assertions.assertDoesNotThrow(() -> first.set(
                service.create(
                        NLFirstName.of(firstName()),
                        NLLastName.of(lastName()),
                        NLEmail.of(email()),
                        NLPassword.of("Ma77ching94$$wor3")
                )
        ));

        Assertions.assertDoesNotThrow(() -> second.set(
                service.create(
                        NLFirstName.of(firstName()),
                        NLLastName.of(lastName()),
                        NLEmail.of(email()),
                        NLPassword.of("Ma77ching94$$wor3")
                )
        ));

        Assertions.assertNotNull(first);
        Assertions.assertTrue(first.get().getValue().startsWith("usr_"));
        Assertions.assertNotNull(second);
        Assertions.assertTrue(second.get().getValue().startsWith("usr_"));
    }

    @Test
    void shouldNotCreateUserAndThrowUserAlreadyExistsException() {
        final NLFirstName name = NLFirstName.of(firstName());
        final NLLastName lastName = NLLastName.of(lastName());
        final NLEmail email = NLEmail.of(email());
        final NLPassword password = NLPassword.of("Ma77ching94$$wor3");

        Assertions.assertDoesNotThrow(() -> service.create(name, lastName, email, password));
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> service.create(name, lastName, email, password));
    }

    @Test
    @SuppressWarnings("java:S5778")
        // does not throw any Exception except desired one
    void shouldNotCreateUserAndThrowInvalidUserDataExceptionWhenBlankData() {
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.create(
                NLFirstName.of(""),
                NLLastName.of(""),
                NLEmail.of(""),
                NLPassword.of("")
        ));

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.create(
                NLFirstName.of(null),
                NLLastName.of(null),
                NLEmail.of(null),
                NLPassword.of(null)
        ));
    }

    @Test
    @SuppressWarnings("java:S5778")
        // does not throw any Exception except desired one
    void shouldNotCreateUserAndThrowInvalidUserDataExceptionWhenPasswordDoesNotMatchRegex() {
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.create(
                NLFirstName.of(""),
                NLLastName.of(""),
                NLEmail.of("organ@person"),
                NLPassword.of("Ma77ching94$$wor3")
        ));
    }

    @Test
    void shouldUpdateUserWhenExistsAndValidData() {
        final NLUser standard = factory.standard();
        final Optional<NLUser> optionalUser = userRepository.findById(standard.getId());

        if (optionalUser.isEmpty()) {
            Assertions.fail();
        }

        final NLUser user = optionalUser.get();
        final String email = standard.getEmail().getValue();
        final UserUpdateRequest request = new UserUpdateRequest(email, secretOrAppKey(), secretOrAppKey(), smtpAccount());

        Assertions.assertDoesNotThrow(() -> service.update(request));
        Assertions.assertEquals(request.appKey(), passwordEncoder.decrypt(user.getAppKey().getValue()));
        Assertions.assertEquals(request.secretKey(), passwordEncoder.decrypt(user.getSecretKey().getValue()));
    }

    @Test
    void shouldNotUpdateUserAndThrowInvalidUserDataExceptionWhenExistsAndBlankRequest() {
        UserUpdateRequest nullRequest = new UserUpdateRequest(null, null, null, null);
        UserUpdateRequest blankRequest = new UserUpdateRequest("   ", "  ", " ", "");

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.update(null));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.update(nullRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.update(blankRequest));
    }

    @Test
    void shouldNotUpdateUserAndThrowInvalidUserDataExceptionWhenExistsAndInvalidData() {
        final NLUser standard = factory.standard();
        final String email = standard.getEmail().getValue();
        final UserUpdateRequest invalidEmailRequest = new UserUpdateRequest("invalid@email", secretOrAppKey(), secretOrAppKey(), smtpAccount());
        final UserUpdateRequest invalidAKRequest = new UserUpdateRequest(email, secretOrAppKey().substring(5), secretOrAppKey(), smtpAccount());
        final UserUpdateRequest invalidSKRequest = new UserUpdateRequest(email, secretOrAppKey(), secretOrAppKey().substring(5), smtpAccount());
        final UserUpdateRequest invalidSMTPRequest = new UserUpdateRequest(email, secretOrAppKey(), secretOrAppKey(), smtpAccount().substring(5));

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.update(invalidEmailRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.update(invalidAKRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.update(invalidSKRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.update(invalidSMTPRequest));
    }

    @Test
    void shouldNotUpdateUserAndThrowInvalidUserDataExceptionWhenNotExistsAndInvalidData() {
        final UserUpdateRequest invalidAKRequest = new UserUpdateRequest(email(), null, secretOrAppKey(), smtpAccount());
        final UserUpdateRequest invalidSKRequest = new UserUpdateRequest(email(), secretOrAppKey(), null, smtpAccount());
        final UserUpdateRequest invalidSMTPRequest = new UserUpdateRequest(email(), secretOrAppKey(), secretOrAppKey(), null);
        final UserUpdateRequest nonExistingUserRequest = new UserUpdateRequest(email(), secretOrAppKey(), secretOrAppKey(), smtpAccount());

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.update(invalidAKRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.update(invalidSKRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.update(invalidSMTPRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.update(nonExistingUserRequest));
    }

    @Test
    void shouldDeleteUserWhenUserExistsAndCorrectData() {
        final NLUuid standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalUser = userRepository.findById(standardUserId);
        if (optionalUser.isEmpty()) {
            Assertions.fail();
        }

        final UserDeleteRequest deleteRequest = new UserDeleteRequest(standardUserId.getValue(), factory.standard().getPassword());
        Assertions.assertDoesNotThrow(() -> service.delete(deleteRequest));
        Assertions.assertEquals(Optional.empty(), userRepository.findById(standardUserId));
    }

    @Test
    void shouldNotDeleteUserAndThrowInvalidUserDataExceptionWhenBlankRequest() {
        final NLUuid standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalUser = userRepository.findById(standardUserId);

        if (optionalUser.isEmpty()) {
            Assertions.fail();
        }

        final UserDeleteRequest nullDataRequest = new UserDeleteRequest(null, null);
        final UserDeleteRequest blankDataRequest = new UserDeleteRequest("", " ");
        final UserDeleteRequest nullIdRequest = new UserDeleteRequest(null, factory.standard().getPassword());

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.delete(null));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.delete(nullDataRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.delete(blankDataRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.delete(nullIdRequest));
        Assertions.assertEquals(optionalUser, userRepository.findById(standardUserId));
    }

    @Test
    void shouldNotDeleteUserAndThrowInvalidUserDataExceptionWhenIncorrectIdAndCorrectPassword() {
        final NLUuid standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalUser = userRepository.findById(standardUserId);

        if (optionalUser.isEmpty()) {
            Assertions.fail();
        }

        final NLUser user = optionalUser.get();
        final UserDeleteRequest notUuidRequest = new UserDeleteRequest("nnw5970167SGikIAq2IbEU7126927", user.getPassword());
        final UserDeleteRequest randomIdRequest = new UserDeleteRequest(UUID.randomUUID().toString(), factory.standard().getPassword());

        Assertions.assertEquals(standardUserId, user.getId());
        Assertions.assertThrows(ValidationException.class, () -> service.delete(notUuidRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.delete(randomIdRequest));
        Assertions.assertEquals(optionalUser, userRepository.findById(standardUserId));
    }

    @Test
    void shouldNotDeleteUserAndThrowInvalidUserDataExceptionWhenCorrectIdAndIncorrectPassword() {
        final NLUuid standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalUser = userRepository.findById(standardUserId);

        if (optionalUser.isEmpty()) {
            Assertions.fail();
        }

        final UserDeleteRequest nullPasswordRequest = new UserDeleteRequest(standardUserId.getValue(), null);
        final UserDeleteRequest blankPasswordRequest = new UserDeleteRequest(standardUserId.getValue(), "  ");
        final UserDeleteRequest invalidPasswordRequest = new UserDeleteRequest(standardUserId.getValue(), "}#$$&643V8@");
        final UserDeleteRequest incorrectPasswordRequest = new UserDeleteRequest(standardUserId.getValue(), factory.dotted_plainPassword());

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.delete(nullPasswordRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.delete(blankPasswordRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.delete(invalidPasswordRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.delete(incorrectPasswordRequest));
        Assertions.assertEquals(optionalUser, userRepository.findById(standardUserId));
    }
}
