package pl.newsler.components.emaillabs;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.client.RestTemplate;
import pl.newsler.commons.model.NLAppKey;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.executor.ELAInstantMailDetails;
import pl.newsler.components.emaillabs.usecase.ELAInstantMailRequest;
import pl.newsler.components.receiver.IReceiverService;
import pl.newsler.components.receiver.StubReceiverModuleConfiguration;
import pl.newsler.components.receiver.StubReceiverRepository;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.security.StubNLPasswordEncoder;
import pl.newsler.testcommons.TestUserUtils;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

class ELATaskInstantExecutorTest {
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubELAMailRepository mailRepository = new StubELAMailRepository();
    private final StubUserRepository userRepository = new StubUserRepository();
    private final IReceiverService receiverService = new StubReceiverModuleConfiguration(new StubReceiverRepository(), userRepository).receiverService();
    private final ELAMailModuleConfiguration configuration = new ELAMailModuleConfiguration(userRepository, mailRepository, passwordEncoder, receiverService);
    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final TestUserFactory factory = new TestUserFactory();
    private final Queue<Pair<NLUuid, ELAInstantMailDetails>> queue = new ConcurrentLinkedQueue<>();
    private final ELATaskInstantExecutor executor = new ELATaskInstantExecutor(
            queue,
            new ConcurrentTaskExecutor(),
            passwordEncoder,
            mailRepository,
            receiverService,
            userRepository,
            restTemplate,
            configuration.elaRequestBuilder()
    );

    @BeforeEach
    void beforeEach() {
        final NLUuid standardId = NLUuid.of(UUID.randomUUID());
        factory.standard().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.standard_plainPassword())));
        factory.standard().setId(standardId);
        factory.standard().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.standard().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.standard().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        userRepository.save(factory.standard());

        final NLUuid dashedId = NLUuid.of(UUID.randomUUID());
        factory.dashed().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dashed_plainPassword())));
        factory.dashed().setId(dashedId);
        factory.dashed().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dashed().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dashed().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        userRepository.save(factory.dashed());

        final NLUuid dottedId = NLUuid.of(UUID.randomUUID());
        factory.dotted().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dotted_plainPassword())));
        factory.dotted().setId(dottedId);
        factory.dotted().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dotted().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dotted().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        userRepository.save(factory.dotted());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void shouldQueueAndExecuteAllRemaining() {
        final ELAInstantMailRequest first = MailModuleUtil.createInstantMailRequest(factory.standard());
        final ELAInstantMailRequest second = MailModuleUtil.createInstantMailRequest(factory.dashed());
        final ELAInstantMailRequest third = MailModuleUtil.createInstantMailRequest(factory.dotted());

        Assertions.assertEquals(0, mailRepository.findAll().size());
        Assertions.assertDoesNotThrow(() -> executor.queue(factory.standard().map().getId(), ELAInstantMailDetails.of(first)));
        Assertions.assertDoesNotThrow(() -> executor.queue(factory.dashed().map().getId(), ELAInstantMailDetails.of(second)));
        Assertions.assertDoesNotThrow(() -> executor.queue(factory.dotted().map().getId(), ELAInstantMailDetails.of(third)));
        Assertions.assertEquals(3, mailRepository.findAll().size());
    }
}