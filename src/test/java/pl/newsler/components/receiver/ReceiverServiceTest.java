package pl.newsler.components.receiver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import pl.newsler.commons.exception.InvalidReceiverDataException;
import pl.newsler.commons.exception.ValidationException;
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
import java.util.concurrent.atomic.AtomicReference;

import static pl.newsler.testcommons.TestUserUtils.email;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.lastName;
import static pl.newsler.testcommons.TestUserUtils.username;

class ReceiverServiceTest {
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
    private final IReceiverService service = configuration.receiverService();

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
        final int size = receiverRepository.findAll().size();
        final ReceiverCreateRequest validCreateRequest = new ReceiverCreateRequest(
                factory.standard().map().getId().getValue(), email(), firstName(), firstName(), lastName()
        );
        Assertions.assertDoesNotThrow(() -> service.add(validCreateRequest, false));
        Assertions.assertEquals(size +1, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotAddReceiverWhenDataInvalid() {
        final int size = receiverRepository.findAll().size();
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

        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.add(invalidCreateRequest, false));
        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.add(invalidCreateRequestModelsNull, false));
        Assertions.assertEquals(size, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotAddReceiverWhenRequestNull() {
        final int size = receiverRepository.findAll().size();

        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.add(null, false));
        Assertions.assertEquals(size, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotAddReceiverWhenRequestDataNull() {
        final int size = receiverRepository.findAll().size();
        final ReceiverCreateRequest nullCreateRequest = new ReceiverCreateRequest(null, null, null, null, null);

        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.add(nullCreateRequest, false));
        Assertions.assertEquals(size, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotAddReceiverWhenRequestDataEmpty() {
        final int size = receiverRepository.findAll().size();
        final ReceiverCreateRequest emptyCreateRequest = new ReceiverCreateRequest("", "", "", "", "");

        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.add(emptyCreateRequest, false));
        Assertions.assertEquals(size, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotAddReceiverWhenUserNotFound() {
        final int size = receiverRepository.findAll().size();
        final ReceiverCreateRequest validCreateRequest = new ReceiverCreateRequest(UUID.randomUUID().toString(), email(), firstName(), firstName(), lastName());

        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.add(validCreateRequest, false));
        Assertions.assertEquals(size, receiverRepository.findAll().size());
    }

    @Test
    void shouldFetchAllUserReceiversWhenUserUuidValidAndUserExists() {
        final int size = receiverRepository.findAll().size();
        final AtomicReference<List<ReceiverGetResponse>> fetchedReceivers = new AtomicReference<>(null);

        Assertions.assertDoesNotThrow(() -> fetchedReceivers.set(service.fetchAllUserReceivers(factory.standard().map().getId().getValue())));
        Assertions.assertEquals(size, fetchedReceivers.get().size());
    }

    @Test
    void shouldNotFetchAllUserReceiversWhenUserUuidValidAndUserNotExists() {
        final String uuid = NLUuid.of(UUID.randomUUID().toString()).toString();

        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.fetchAllUserReceivers(uuid));
    }

    @Test
    void shouldNotFetchAllUserReceiversWhenUserUuidInvalid() {
        Assertions.assertThrows(ValidationException.class, () -> service.fetchAllUserReceivers("uuid"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotFetchAllUserReceiversWhenUserUuidEmptyOrNull(String uuid) {
        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.fetchAllUserReceivers(uuid));
    }

    @Test
    void shouldNotFetchAllUserReceiversWhenUserUuidBlank() {
        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.fetchAllUserReceivers("  "));
    }
}