package pl.newsler.components.emaillabs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.model.NLAppKey;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.exception.InvalidDateException;
import pl.newsler.components.emaillabs.executor.ELAInstantMailDetails;
import pl.newsler.components.emaillabs.usecase.ELAGetMailResponse;
import pl.newsler.components.emaillabs.usecase.ELAInstantMailRequest;
import pl.newsler.components.emaillabs.usecase.ELAScheduleMailRequest;
import pl.newsler.components.receiver.IReceiverService;
import pl.newsler.components.receiver.StubReceiverModuleConfiguration;
import pl.newsler.components.receiver.StubReceiverRepository;
import pl.newsler.components.user.NLDUser;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.security.StubNLPasswordEncoder;
import pl.newsler.testcommons.TestUserUtils;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static pl.newsler.components.emaillabs.MailModuleUtil.createInstantMailRequest;

public class ELAMailServiceTest {
    private static final int TIME_OFFSET = 100_000;
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubELAMailRepository mailRepository = new StubELAMailRepository();
    private final StubUserRepository userRepository = new StubUserRepository();
    private final IReceiverService receiverService = new StubReceiverModuleConfiguration(new StubReceiverRepository(), userRepository).receiverService();
    private final ELAMailModuleConfiguration configuration = new ELAMailModuleConfiguration(userRepository, mailRepository, passwordEncoder, receiverService);
    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final IELAMailService service = configuration.mailService(
            configuration.taskInstantExecutor(restTemplate, configuration.elaRequestBuilder()),
            configuration.taskScheduledExecutor(restTemplate, configuration.elaRequestBuilder())
    );
    private final TestUserFactory factory = new TestUserFactory();

    @BeforeEach
    void beforeEach() {
        final NLUuid standardId = NLUuid.of(UUID.randomUUID());
        factory.standard().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.standard_plainPassword())));
        factory.standard().setUuid(standardId);
        factory.standard().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.standard().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.standard().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        userRepository.save(factory.standard());

        final NLUuid dashedId = NLUuid.of(UUID.randomUUID());
        factory.dashed().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dashed_plainPassword())));
        factory.dashed().setUuid(dashedId);
        factory.dashed().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dashed().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dashed().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        userRepository.save(factory.dashed());

        final NLUuid dottedId = NLUuid.of(UUID.randomUUID());
        factory.dotted().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dotted_plainPassword())));
        factory.dotted().setUuid(dottedId);
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
    void shouldQueueMailExecutionAndExecuteIt() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = users.get(0);
        final ELAInstantMailRequest request = createInstantMailRequest(user);

        Assertions.assertDoesNotThrow(() -> service.queue(request));
    }

    @Test
    void shouldNotQueueMailWhenRequesterEmailInvalid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final ELAInstantMailRequest request = new ELAInstantMailRequest(
                TestUserUtils.email(),
                List.of(users.get(0).getEmail().getValue(), users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
        );

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.queue(request));
    }

    @Test
    void shouldScheduleMailWhenRequestValid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }


        final NLUser user = users.get(0);
        final ELAScheduleMailRequest request = MailModuleUtil.createScheduledMailRequest(
                user,
                System.currentTimeMillis() + TIME_OFFSET,
                ZoneId.systemDefault().toString()
        );

        Assertions.assertDoesNotThrow(() -> service.schedule(request));
        Assertions.assertEquals(1, mailRepository.findAll().size());
    }

    @Test
    void shouldScheduleMailWhenRequestValidButNoZoneFound() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = users.get(0);
        final ELAScheduleMailRequest request = MailModuleUtil.createScheduledMailRequest(
                user,
                System.currentTimeMillis() + TIME_OFFSET,
                "Zone"
        );

        Assertions.assertDoesNotThrow(() -> service.schedule(request));
        Assertions.assertEquals(1, mailRepository.findAll().size());
    }

    @Test
    void shouldNotScheduleMailAndThrowInvalidUserDateExceptionWhenUserNotFound() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = NLDUser.of(users.get(0)).toUser();
        user.setEmail(NLEmail.of(TestUserUtils.email()));

        final ELAScheduleMailRequest request = MailModuleUtil.createScheduledMailRequest(
                user,
                System.currentTimeMillis() + TIME_OFFSET,
                "Zone"
        );

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.schedule(request));
        Assertions.assertEquals(0, mailRepository.findAll().size());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void shouldNotScheduleMailAndThrowInvalidDateExceptionWhenDateTimeBlank(Long timestamp) {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = users.get(0);
        final ELAScheduleMailRequest request = MailModuleUtil.createScheduledMailRequest(
                user,
                timestamp,
                "Zone"
        );

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.schedule(request));
        Assertions.assertEquals(0, mailRepository.findAll().size());
    }

    @ParameterizedTest
    @ValueSource(longs = {16796, 16770177})
    void shouldNotScheduleMailAndThrowInvalidDateExceptionWhenInvalidDate_DateTimeException(Long timestamp) {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = users.get(0);
        final ELAScheduleMailRequest request = MailModuleUtil.createScheduledMailRequest(
                user,
                timestamp,
                "Zone"
        );

        Assertions.assertThrows(InvalidDateException.class, () -> service.schedule(request));
        Assertions.assertEquals(0, mailRepository.findAll().size());
    }

    @Test
    void shouldFetchAllMailsAndReturn200_OkWhenUserIdValid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = users.get(0);
        final ELAInstantMailRequest first = createInstantMailRequest(user);
        final ELAInstantMailRequest second = createInstantMailRequest(user);
        final ELAInstantMailRequest third = createInstantMailRequest(user);

        mailRepository.save(ELAUserMail.of(user.map().getUuid(), ELAInstantMailDetails.of(first)));
        mailRepository.save(ELAUserMail.of(user.map().getUuid(), ELAInstantMailDetails.of(second)));
        mailRepository.save(ELAUserMail.of(user.map().getUuid(), ELAInstantMailDetails.of(third)));

        final List<ELAGetMailResponse> fetchMails = service.fetchAllMails(user.map().getUuid());
        Assertions.assertNotNull(fetchMails);
        Assertions.assertEquals(3, fetchMails.size());
    }

    @Test
    void shouldNotFetchAllMailsAndReturn400_BadRequestWhenUserIdInvalid() {
        final NLUuid uuid = NLUuid.of(UUID.randomUUID());
        Assertions.assertThrows(InvalidUserDataException.class, () -> service.fetchAllMails(uuid));
    }
}
