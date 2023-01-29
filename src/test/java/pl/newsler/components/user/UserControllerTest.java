package pl.newsler.components.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import pl.newsler.api.IUserController;
import pl.newsler.commons.exception.GlobalRestExceptionHandler;
import pl.newsler.commons.exception.NLError;
import pl.newsler.commons.exception.NLException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserGetRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;
import pl.newsler.security.StubNLPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static pl.newsler.testcommons.TestUserUtils.email;
import static pl.newsler.testcommons.TestUserUtils.secretOrAppKey;
import static pl.newsler.testcommons.TestUserUtils.smtpAccount;

class UserControllerTest {
    private final GlobalRestExceptionHandler handler = new GlobalRestExceptionHandler();
    private final TestUserFactory factory = new TestUserFactory();
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubUserRepository userRepositoryMock = new StubUserRepository();
    private final UserModuleConfiguration configuration = new UserModuleConfiguration(
            userRepositoryMock,
            passwordEncoder
    );
    private final IUserCrudService service = configuration.userService();
    private final IUserController controller = new UserController(service);

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
        userRepositoryMock.deleteAll();
    }


    @Test
    void shouldGetUserWhenValidRequest() {
        NLUser user = factory.dashed();
        UserGetRequest request = new UserGetRequest(user.getEmail().getValue(), user.getPassword());

        handleResponse(request, HttpStatus.OK);
    }

    @Test
    void shouldNotGetUserAndThrowInvalidUserDataExceptionWhenPasswordDoesNotMatchUsersPassword() {
        final UserGetRequest blankRequest = new UserGetRequest(factory.dashed().getEmail().getValue(), "Ma77ching94$$wor3");

        handleResponse(blankRequest, HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotGetUserAndThrowInvalidUserDataExceptionWhenBlankData() {
        final UserGetRequest nullRequest = new UserGetRequest(null, null);
        final UserGetRequest blankRequest = new UserGetRequest(" ", "");

        handleResponse((UserGetRequest) null, HttpStatus.BAD_REQUEST);
        handleResponse(nullRequest, HttpStatus.BAD_REQUEST);
        handleResponse(blankRequest, HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotGetUserAndThrowInvalidUserDataExceptionWhenDataInvalid() {
        final UserGetRequest invalidDataRequest = new UserGetRequest("invalid@email", "invalid$password");
        final UserGetRequest invalidPasswordRequest = new UserGetRequest("valid@email.in", "invalid$password");
        final UserGetRequest notFoundUserRequest = new UserGetRequest(email(), "%**P26AZs>^#!Gp<");

        handleResponse(invalidDataRequest, HttpStatus.BAD_REQUEST);
        handleResponse(invalidPasswordRequest, HttpStatus.BAD_REQUEST);
        handleResponse(notFoundUserRequest, HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldUpdateUserWhenExistsAndValidData() {
        final NLUser standard = factory.standard();
        final Optional<NLUser> optionalUser = userRepositoryMock.findById(standard.getId());

        if (optionalUser.isEmpty()) {
            Assertions.fail();
        }

        final String email = standard.getEmail().getValue();
        final UserUpdateRequest request = new UserUpdateRequest(email, secretOrAppKey(), secretOrAppKey(), smtpAccount());

        handleResponse(request, HttpStatus.OK);
    }

    @Test
    void shouldNotUpdateUserAndThrowInvalidUserDataExceptionWhenExistsAndBlankRequest() {
        UserUpdateRequest nullRequest = new UserUpdateRequest(null, null, null, null);
        UserUpdateRequest blankRequest = new UserUpdateRequest("   ", "  ", " ", "");

        handleResponse((UserUpdateRequest) null, HttpStatus.BAD_REQUEST);
        handleResponse(nullRequest, HttpStatus.BAD_REQUEST);
        handleResponse(blankRequest, HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotUpdateUserAndThrowInvalidUserDataExceptionWhenExistsAndInvalidData() {
        final NLUser standard = factory.standard();
        final String email = standard.getEmail().getValue();
        final UserUpdateRequest invalidEmailRequest = new UserUpdateRequest("invalid@email", secretOrAppKey(), secretOrAppKey(), smtpAccount());
        final UserUpdateRequest invalidAKRequest = new UserUpdateRequest(email, secretOrAppKey().substring(5), secretOrAppKey(), smtpAccount());
        final UserUpdateRequest invalidSKRequest = new UserUpdateRequest(email, secretOrAppKey(), secretOrAppKey().substring(5), smtpAccount());
        final UserUpdateRequest invalidSMTPRequest = new UserUpdateRequest(email, secretOrAppKey(), secretOrAppKey(), smtpAccount().substring(5));

        handleResponse(invalidEmailRequest, HttpStatus.BAD_REQUEST);
        handleResponse(invalidAKRequest, HttpStatus.BAD_REQUEST);
        handleResponse(invalidSKRequest, HttpStatus.BAD_REQUEST);
        handleResponse(invalidSMTPRequest, HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotUpdateUserAndThrowInvalidUserDataExceptionWhenNotExistsAndInvalidData() {
        final UserUpdateRequest invalidAKRequest = new UserUpdateRequest(email(), null, secretOrAppKey(), smtpAccount());
        final UserUpdateRequest invalidSKRequest = new UserUpdateRequest(email(), secretOrAppKey(), null, smtpAccount());
        final UserUpdateRequest invalidSMTPRequest = new UserUpdateRequest(email(), secretOrAppKey(), secretOrAppKey(), null);
        final UserUpdateRequest nonExistingUserRequest = new UserUpdateRequest(email(), secretOrAppKey(), secretOrAppKey(), smtpAccount());

        handleResponse(invalidAKRequest, HttpStatus.BAD_REQUEST);
        handleResponse(invalidSKRequest, HttpStatus.BAD_REQUEST);
        handleResponse(invalidSMTPRequest, HttpStatus.BAD_REQUEST);
        handleResponse(nonExistingUserRequest, HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldDeleteUserWhenUserExistsAndCorrectData() {
        final NLUuid standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalUser = userRepositoryMock.findById(standardUserId);
        if (optionalUser.isEmpty()) {
            Assertions.fail();
        }

        final UserDeleteRequest deleteRequest = new UserDeleteRequest(standardUserId.getValue(), factory.standard().getPassword());
        handleResponse(deleteRequest, HttpStatus.OK);
    }

    @Test
    void shouldNotDeleteUserAndThrowInvalidUserDataExceptionWhenBlankRequest() {
        final NLUuid standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalUser = userRepositoryMock.findById(standardUserId);

        if (optionalUser.isEmpty()) {
            Assertions.fail();
        }

        final UserDeleteRequest nullDataRequest = new UserDeleteRequest(null, null);
        final UserDeleteRequest blankDataRequest = new UserDeleteRequest("", " ");
        final UserDeleteRequest nullIdRequest = new UserDeleteRequest(null, factory.standard().getPassword());

        handleResponse((UserDeleteRequest) null, HttpStatus.BAD_REQUEST);
        handleResponse(nullDataRequest, HttpStatus.BAD_REQUEST);
        handleResponse(blankDataRequest, HttpStatus.BAD_REQUEST);
        handleResponse(nullIdRequest, HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotDeleteUserAndThrowInvalidUserDataExceptionWhenIncorrectIdAndCorrectPassword() {
        final NLUuid standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalUser = userRepositoryMock.findById(standardUserId);

        if (optionalUser.isEmpty()) {
            Assertions.fail();
        }

        final NLUser user = optionalUser.get();
        final UserDeleteRequest notUuidRequest = new UserDeleteRequest("nnw5970167SGikIAq2IbEU7126927", user.getPassword());
        final UserDeleteRequest randomIdRequest = new UserDeleteRequest(UUID.randomUUID().toString(), factory.standard().getPassword());

        handleResponse(notUuidRequest, HttpStatus.BAD_REQUEST);
        handleResponse(randomIdRequest, HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotDeleteUserAndThrowInvalidUserDataExceptionWhenCorrectIdAndIncorrectPassword() {
        final NLUuid standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalUser = userRepositoryMock.findById(standardUserId);

        if (optionalUser.isEmpty()) {
            Assertions.fail();
        }

        final UserDeleteRequest nullPasswordRequest = new UserDeleteRequest(standardUserId.getValue(), null);
        final UserDeleteRequest blankPasswordRequest = new UserDeleteRequest(standardUserId.getValue(), "  ");
        final UserDeleteRequest invalidPasswordRequest = new UserDeleteRequest(standardUserId.getValue(), "}#$$&643V8@");
        final UserDeleteRequest incorrectPasswordRequest = new UserDeleteRequest(standardUserId.getValue(), "Pa$$word7hat^match3$");

        handleResponse(nullPasswordRequest, HttpStatus.BAD_REQUEST);
        handleResponse(blankPasswordRequest, HttpStatus.BAD_REQUEST);
        handleResponse(invalidPasswordRequest, HttpStatus.BAD_REQUEST);
        handleResponse(incorrectPasswordRequest, HttpStatus.BAD_REQUEST);
    }

    private void handleResponse(final UserGetRequest request, final HttpStatus expectedStatus) {
        try {
            ResponseEntity<NLDUser> response = controller.get(request);
            Assertions.assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            if (e instanceof NLException nle) {
                ResponseEntity<NLError> entity = handler.handleException(nle);
                Assertions.assertNotNull(entity);
                Assertions.assertEquals(expectedStatus, entity.getStatusCode());
            } else {
                Assertions.fail("Not a NLException");
            }
        }
    }

    private void handleResponse(final UserUpdateRequest request, final HttpStatus expectedStatus) {
        try {
            ResponseEntity<String> response = controller.update(request);
            Assertions.assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            if (e instanceof NLException nle) {
                ResponseEntity<NLError> entity = handler.handleException(nle);
                Assertions.assertNotNull(entity);
                Assertions.assertEquals(expectedStatus, entity.getStatusCode());
            } else {
                Assertions.fail("Not a NLException");
            }
        }
    }

    private void handleResponse(final UserDeleteRequest request, final HttpStatus expectedStatus) {
        try {
            ResponseEntity<String> response = controller.delete(request);
            Assertions.assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            if (e instanceof NLException nle) {
                ResponseEntity<NLError> entity = handler.handleException(nle);
                Assertions.assertNotNull(entity);
                Assertions.assertEquals(expectedStatus, entity.getStatusCode());
            } else {
                Assertions.fail("Not a NLException");
            }
        }
    }
}