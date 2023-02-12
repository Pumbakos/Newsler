package pl.newsler.components.receiver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.api.IReceiverController;
import pl.newsler.commons.exception.GlobalRestExceptionHandler;
import pl.newsler.commons.exception.NLError;
import pl.newsler.commons.exception.NLException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLNickname;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLVersion;
import pl.newsler.components.receiver.dto.ReceiverCreateRequest;
import pl.newsler.components.receiver.dto.ReceiverGetResponse;
import pl.newsler.components.user.IUserCrudService;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.StubUserModuleConfiguration;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.security.StubNLPasswordEncoder;

import java.util.List;
import java.util.UUID;

import static pl.newsler.testcommons.TestUserUtils.email;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.lastName;
import static pl.newsler.testcommons.TestUserUtils.username;

class ReceiverControllerTest {
    private final GlobalRestExceptionHandler handler = new GlobalRestExceptionHandler();
    private final TestUserFactory factory = new TestUserFactory();
    private final IUserRepository userRepository = new StubUserRepository();
    private final IReceiverRepository receiverRepository = new StubReceiverRepository();
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubUserModuleConfiguration userConfiguration = new StubUserModuleConfiguration(
            userRepository,
            passwordEncoder
    );
    private final ReceiverModuleConfiguration configuration = new ReceiverModuleConfiguration(receiverRepository, userRepository);
    private final IUserCrudService crudService = userConfiguration.userService();
    private final IReceiverController controller = new ReceiverController(configuration.receiverService());

    @BeforeEach
    void beforeEach() {
        NLUuid uuid = crudService.create(
                NLFirstName.of(factory.standard().getFirstName().getValue()),
                NLLastName.of(factory.standard().getLastName().getValue()),
                NLEmail.of(factory.standard().getEmail().getValue()),
                NLPassword.of(factory.standard().getNLPassword().getValue())
        );
        factory.standard().setId(uuid);

        receiverRepository.save(new Receiver(
                NLUuid.of(UUID.randomUUID()), NLVersion.of("0.0.0TEST"), uuid, NLEmail.of(email()),
                NLNickname.of(username()), NLFirstName.of(firstName()), NLLastName.of(lastName()), false
        ));

        receiverRepository.save(new Receiver(
                NLUuid.of(UUID.randomUUID()), NLVersion.of("0.0.0TEST"), uuid, NLEmail.of(email()),
                NLNickname.of(username()), NLFirstName.of(firstName()), NLLastName.of(lastName()), false
        ));

        receiverRepository.save(new Receiver(
                NLUuid.of(UUID.randomUUID()), NLVersion.of("0.0.0TEST"), uuid, NLEmail.of(email()),
                NLNickname.of(username()), NLFirstName.of(firstName()), NLLastName.of(lastName()), false
        ));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        receiverRepository.deleteAll();
    }

    @Test
    void shouldAddReceiverWhenDataValid() {
        final ReceiverCreateRequest validCreateRequest = new ReceiverCreateRequest(
                factory.standard().map().getId().getValue(), email(), firstName(), firstName(), lastName()
        );

        try {
            ResponseEntity<String> response = controller.addReceiver(validCreateRequest);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        } catch (NLException e) {
            Assertions.fail();
        }
    }

    @Test
    void shouldNotAddReceiverWhenDataInvalid() {
        final ReceiverCreateRequest invalidCreateRequest = new ReceiverCreateRequest(
                factory.standard().map().getId().getValue(),
                username(),
                username(),
                username(),
                username()
        );
        final ReceiverCreateRequest invalidCreateRequestModelsNull = new ReceiverCreateRequest(
                factory.standard().map().getId().getValue(), null, null, null, null
        );

        try {
            controller.addReceiver(invalidCreateRequest);
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        try {
            controller.addReceiver(invalidCreateRequestModelsNull);
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void shouldNotAddReceiverWhenRequestNull() {
        try {
            controller.addReceiver(null);
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void shouldNotAddReceiverWhenRequestDataNull() {
        final ReceiverCreateRequest nullCreateRequest = new ReceiverCreateRequest(null, null, null, null, null);

        try {
            controller.addReceiver(nullCreateRequest);
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void shouldNotAddReceiverWhenRequestDataEmpty() {
        final ReceiverCreateRequest emptyCreateRequest = new ReceiverCreateRequest("", "", "", "", "");

        try {
            controller.addReceiver(emptyCreateRequest);
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void shouldNotAddReceiverWhenUserNotFound() {
        final ReceiverCreateRequest validCreateRequest = new ReceiverCreateRequest(UUID.randomUUID().toString(), email(), firstName(), firstName(), lastName());

        try {
            controller.addReceiver(validCreateRequest);
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void shouldFetchAllUserReceiversWhenUserUuidValidAndUserExists() {
        try {
            ResponseEntity<List<ReceiverGetResponse>> response = controller.fetchAllUserReceivers(factory.standard().map().getId().getValue());
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        } catch (NLException e) {
            Assertions.fail();
        }
    }

    @Test
    void shouldNotFetchAllUserReceiversWhenUserUuidValidAndUserNotExists() {
        final String uuid = NLUuid.of(UUID.randomUUID().toString()).toString();

        try {
            controller.fetchAllUserReceivers(uuid);
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void shouldNotFetchAllUserReceiversWhenUserUuidInvalid() {
        try {
            controller.fetchAllUserReceivers("uuid");
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotFetchAllUserReceiversWhenUserUuidEmptyOrNull(String uuid) {
        try {
            controller.fetchAllUserReceivers(uuid);
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void shouldNotFetchAllUserReceiversWhenUserUuidBlank() {
        try {
            controller.fetchAllUserReceivers("  ");
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
}