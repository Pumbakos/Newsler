package pl.newsler.components.receiver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import pl.newsler.commons.exception.InvalidReceiverDataException;
import pl.newsler.commons.exception.ValidationException;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLNickname;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.commons.model.NLVersion;
import pl.newsler.components.emaillabs.StubELAMailModuleConfiguration;
import pl.newsler.components.emaillabs.StubELAMailRepository;
import pl.newsler.components.receiver.exception.ReceiverAlreadySubscribedException;
import pl.newsler.components.receiver.usecase.ReceiverCreateRequest;
import pl.newsler.components.receiver.usecase.ReceiverGetResponse;
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
    private final ReceiverModuleConfiguration configuration = new ReceiverModuleConfiguration(receiverRepository, userRepository);
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubELAMailRepository mailRepository = new StubELAMailRepository();
    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final StubELAMailModuleConfiguration mailModuleConfiguration = new StubELAMailModuleConfiguration(
            userRepository,
            mailRepository,
            passwordEncoder,
            configuration.receiverService()
    );
    private final StubUserModuleConfiguration userModuleConfiguration = new StubUserModuleConfiguration(
            userRepository,
            passwordEncoder,
            mailModuleConfiguration.templateService(mailModuleConfiguration.elaParamBuilder(), restTemplate)
    );
    private final IUserCrudService crudService = userModuleConfiguration.userService();
    private final IReceiverService service = configuration.receiverService();

    @BeforeEach
    void beforeEach() {
        NLUuid uuid = crudService.create(
                NLFirstName.of(factory.standard().getFirstName().getValue()),
                NLLastName.of(factory.standard().getLastName().getValue()),
                NLEmail.of(factory.standard().getEmail().getValue()),
                NLPassword.of(factory.standard().getNLPassword().getValue())
        );
        factory.standard().setUuid(uuid);

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
                factory.standard().map().getUuid().getValue(), email(), firstName(), firstName(), lastName()
        );
        Assertions.assertDoesNotThrow(() -> service.addReceiver(validCreateRequest, false));
        Assertions.assertEquals(size + 1, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotAddReceiverWhenDataInvalid() {
        final int size = receiverRepository.findAll().size();
        final ReceiverCreateRequest invalidCreateRequest = new ReceiverCreateRequest(
                factory.standard().map().getUuid().getValue(),
                username(),
                username(),
                username(),
                username()
        );
        final ReceiverCreateRequest invalidCreateRequestModelsNull = new ReceiverCreateRequest(
                factory.standard().map().getUuid().getValue(), null, null, null, null
        );

        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.addReceiver(invalidCreateRequest, false));
        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.addReceiver(invalidCreateRequestModelsNull, false));
        Assertions.assertEquals(size, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotAddReceiverWhenRequestNull() {
        final int size = receiverRepository.findAll().size();

        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.addReceiver(null, false));
        Assertions.assertEquals(size, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotAddReceiverWhenRequestDataNull() {
        final int size = receiverRepository.findAll().size();
        final ReceiverCreateRequest nullCreateRequest = new ReceiverCreateRequest(null, null, null, null, null);

        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.addReceiver(nullCreateRequest, false));
        Assertions.assertEquals(size, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotAddReceiverWhenRequestDataEmpty() {
        final int size = receiverRepository.findAll().size();
        final ReceiverCreateRequest emptyCreateRequest = new ReceiverCreateRequest("", "", "", "", "");

        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.addReceiver(emptyCreateRequest, false));
        Assertions.assertEquals(size, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotAddReceiverWhenUserNotFound() {
        final int size = receiverRepository.findAll().size();
        final ReceiverCreateRequest validCreateRequest = new ReceiverCreateRequest(UUID.randomUUID().toString(), email(), firstName(), firstName(), lastName());

        Assertions.assertThrows(InvalidReceiverDataException.class, () -> service.addReceiver(validCreateRequest, false));
        Assertions.assertEquals(size, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotAddReceiverWhenReceiverAlreadyAssociatedWithUserAndWasNotAutoSaved() {
        final ReceiverCreateRequest validCreateRequest = new ReceiverCreateRequest(
                factory.standard().map().getUuid().getValue(), email(), firstName(), firstName(), lastName()
        );
        service.addReceiver(validCreateRequest, false);
        final int size = receiverRepository.findAll().size();

        Assertions.assertThrows(ReceiverAlreadySubscribedException.class, () -> service.addReceiver(validCreateRequest, false));
        Assertions.assertEquals(size, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotAddReceiverWhenReceiverAlreadyAssociatedWithUserAndWasAutoSaved() {
        final ReceiverCreateRequest validCreateRequest = new ReceiverCreateRequest(
                factory.standard().map().getUuid().getValue(), email(), firstName(), firstName(), lastName()
        );
        service.addReceiver(validCreateRequest, false);
        final int size = receiverRepository.findAll().size();

        Assertions.assertThrows(ReceiverAlreadySubscribedException.class, () -> service.addReceiver(validCreateRequest, true));
        Assertions.assertEquals(size, receiverRepository.findAll().size());
    }

    @Test
    void shouldFetchAllUserReceiversWhenUserUuidValidAndUserExists() {
        final int size = receiverRepository.findAll().size();
        final AtomicReference<List<ReceiverGetResponse>> fetchedReceivers = new AtomicReference<>(null);

        Assertions.assertDoesNotThrow(() -> fetchedReceivers.set(service.fetchAllUserReceivers(factory.standard().map().getUuid().getValue())));
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