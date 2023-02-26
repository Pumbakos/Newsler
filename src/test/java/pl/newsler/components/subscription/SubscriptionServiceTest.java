package pl.newsler.components.subscription;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLNickname;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.receiver.IReceiverRepository;
import pl.newsler.components.receiver.Receiver;
import pl.newsler.components.receiver.StubReceiverRepository;
import pl.newsler.components.subscription.exception.CancellationReceiverException;
import pl.newsler.components.subscription.exception.CancellationTokenException;
import pl.newsler.components.user.IUserCrudService;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.StubUserModuleConfiguration;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.security.StubNLPasswordEncoder;

import java.util.UUID;

import static pl.newsler.testcommons.TestUserUtils.email;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.lastName;

class SubscriptionServiceTest {
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubReceiverRepository receiverRepository = new StubReceiverRepository();
    private final StubUserRepository userRepository = new StubUserRepository();
    private final SubscriptionModuleConfiguration configuration = new SubscriptionModuleConfiguration(
            receiverRepository,
            userRepository
    );
    private final ISubscriptionService service = configuration.subscriptionService();
    private final TestUserFactory factory = new TestUserFactory();

    @BeforeEach
    void beforeEach() {
        final StubUserModuleConfiguration userModuleConfiguration = new StubUserModuleConfiguration(
                userRepository,
                passwordEncoder
        );
        final IUserCrudService userCrudService = userModuleConfiguration.userService();

        factory.standard().setId(
                userCrudService.create(
                        NLFirstName.of(factory.standard().getFirstName().getValue()),
                        NLLastName.of(factory.standard().getLastName().getValue()),
                        NLEmail.of(factory.standard().getEmail().getValue()),
                        NLPassword.of(factory.standard().getNLPassword().getValue())
                ));
        factory.dashed().setId(
                userCrudService.create(
                        NLFirstName.of(factory.dashed().getFirstName().getValue()),
                        NLLastName.of(factory.dashed().getLastName().getValue()),
                        NLEmail.of(factory.dashed().getEmail().getValue()),
                        NLPassword.of(factory.dashed().getNLPassword().getValue())
                ));
        factory.dotted().setId(
                userCrudService.create(
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
    void shouldCancelSubscriptionWhenCancellationTokenValid() {
        final NLEmail email = NLEmail.of(email());
        final NLUuid uuid = factory.standard().map().getId();
        receiverRepository.save(
                new Receiver(NLUuid.of(UUID.randomUUID()), IReceiverRepository.version,
                        uuid, email, NLNickname.of(firstName()),
                        NLFirstName.of(firstName()), NLLastName.of(lastName()), false
                )
        );
        receiverRepository.save(
                new Receiver(NLUuid.of(UUID.randomUUID()), IReceiverRepository.version,
                        uuid, NLEmail.of(email()), NLNickname.of(firstName()),
                        NLFirstName.of(firstName()), NLLastName.of(lastName()), false
                )
        );
        Assertions.assertEquals(2, receiverRepository.findAll().size());
        final NLUser user = userRepository.findById(uuid).get();

        Assertions.assertDoesNotThrow(() -> service.cancel(user.getCancellationToken().getValue(), email.getValue()));
        Assertions.assertEquals(1, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotCancelSubscriptionWhenCancellationTokenInvalid() {
        final NLUser user = factory.standard();
        final NLUuid uuid = user.map().getId();
        final String cancellationToken = user.getCancellationToken().getValue();
        final String email = email();

        receiverRepository.save(
                new Receiver(NLUuid.of(UUID.randomUUID()), IReceiverRepository.version,
                        uuid, NLEmail.of(email), NLNickname.of(firstName()),
                        NLFirstName.of(firstName()), NLLastName.of(lastName()), false
                )
        );
        receiverRepository.save(
                new Receiver(NLUuid.of(UUID.randomUUID()), IReceiverRepository.version,
                        uuid, NLEmail.of(email()), NLNickname.of(firstName()),
                        NLFirstName.of(firstName()), NLLastName.of(lastName()), false
                )
        );

        Assertions.assertEquals(2, receiverRepository.findAll().size());
        Assertions.assertThrows(CancellationTokenException.class, () -> service.cancel(cancellationToken, email));
        Assertions.assertEquals(2, receiverRepository.findAll().size());
    }

    @Test
    void shouldNotCancelSubscriptionWhenCancellationTokenValidButUserEmailNotAssociatedWithUser() {
        final NLEmail email = NLEmail.of(email());
        final NLUuid uuid = factory.standard().map().getId();

        receiverRepository.save(
                new Receiver(NLUuid.of(UUID.randomUUID()), IReceiverRepository.version,
                        uuid, email, NLNickname.of(firstName()),
                        NLFirstName.of(firstName()), NLLastName.of(lastName()), false
                )
        );
        receiverRepository.save(
                new Receiver(NLUuid.of(UUID.randomUUID()), IReceiverRepository.version,
                        uuid, NLEmail.of(email()), NLNickname.of(firstName()),
                        NLFirstName.of(firstName()), NLLastName.of(lastName()), false
                )
        );
        Assertions.assertEquals(2, receiverRepository.findAll().size());

        final NLUser user = userRepository.findById(uuid).get();
        final String cancellationToken = user.getCancellationToken().getValue();
        final String notAssociatedEmail = email();

        Assertions.assertThrows(CancellationReceiverException.class, () -> service.cancel(cancellationToken, notAssociatedEmail));
        Assertions.assertEquals(2, receiverRepository.findAll().size());
    }
}