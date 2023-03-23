package pl.newsler.components.emaillabs;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.client.RestTemplate;
import pl.newsler.commons.model.NLAppKey;
import pl.newsler.commons.model.NLExecutionDate;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.commons.model.NLStringValue;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.executor.ELARequestBuilder;
import pl.newsler.components.emaillabs.executor.ELAScheduleMailDetails;
import pl.newsler.components.emaillabs.usecase.ELAScheduleMailRequest;
import pl.newsler.components.receiver.IReceiverService;
import pl.newsler.components.receiver.StubReceiverModuleConfiguration;
import pl.newsler.components.receiver.StubReceiverRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.internal.NewslerDesignerServiceProperties;
import pl.newsler.security.StubNLPasswordEncoder;
import pl.newsler.testcommons.TestUserUtils;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

class ELATaskScheduledExecutorTest {
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubELAMailRepository mailRepository = new StubELAMailRepository();
    private final StubUserRepository userRepository = new StubUserRepository();
    private final IReceiverService receiverService = new StubReceiverModuleConfiguration(new StubReceiverRepository(), userRepository).receiverService();
    private final ELAMailModuleConfiguration configuration = new ELAMailModuleConfiguration(userRepository, mailRepository, passwordEncoder, receiverService);
    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final TestUserFactory factory = new TestUserFactory();
    private final Random random = new SecureRandom();
    private final Queue<Pair<NLUuid, ELAScheduleMailDetails>> queue = new ConcurrentLinkedQueue<>();
    private final ELARequestBuilder requestBuilder = configuration.elaRequestBuilder();
    private final ELATaskScheduledExecutor executor = new ELATaskScheduledExecutor(
            queue,
            new ConcurrentTaskScheduler(),
            passwordEncoder,
            mailRepository,
            receiverService,
            userRepository,
            restTemplate,
            requestBuilder
    );

    @BeforeEach
    void beforeEach() throws NoSuchFieldException, IllegalAccessException {
        final NLUuid standardId = NLUuid.of(UUID.randomUUID());
        factory.standard().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.standard_plainPassword())));
        factory.standard().setUuid(standardId);
        factory.standard().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.standard().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.standard().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        factory.standard().setDefaultTemplateId(NLStringValue.of("98djd3sa81"));

        final NLUuid dashedId = NLUuid.of(UUID.randomUUID());
        factory.dashed().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dashed_plainPassword())));
        factory.dashed().setUuid(dashedId);
        factory.dashed().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dashed().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dashed().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        factory.dashed().setDefaultTemplateId(NLStringValue.of("98djd3sa81"));

        final NLUuid dottedId = NLUuid.of(UUID.randomUUID());
        factory.dotted().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dotted_plainPassword())));
        factory.dotted().setUuid(dottedId);
        factory.dotted().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dotted().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dotted().setDefaultTemplateId(NLStringValue.of("98djd3sa81"));
        factory.dotted().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        userRepository.saveAll(List.of(factory.dashed(), factory.dotted(), factory.standard()));

        reflectControllerFields();
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void shouldScanQueueAndExecuteAllInQueue() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final LocalDateTime now = LocalDateTime.now().minusMinutes(random.nextInt(10));
        final ZoneId zoneId = ZoneId.systemDefault();
        final NLUser user = users.get(0);

        final ELAScheduleMailRequest first = MailModuleUtil.createScheduledMailRequest(user, now.toInstant(ZoneOffset.UTC).toEpochMilli(), zoneId.toString());
        final ELAScheduleMailRequest second = MailModuleUtil.createScheduledMailRequest(user, now.toInstant(ZoneOffset.UTC).toEpochMilli(), zoneId.toString());
        final ELAScheduleMailRequest third = MailModuleUtil.createScheduledMailRequest(user, now.toInstant(ZoneOffset.UTC).toEpochMilli(), zoneId.toString());

        executor.schedule(user.map().getId(), ELAScheduleMailDetails.of(first, ZonedDateTime.of(now, zoneId)));
        executor.schedule(user.map().getId(), ELAScheduleMailDetails.of(second, ZonedDateTime.of(now, zoneId)));
        executor.schedule(user.map().getId(), ELAScheduleMailDetails.of(third, ZonedDateTime.of(now, zoneId)));
        Assertions.assertEquals(3, queue.size());

        executor.scanQueue();
        Assertions.assertEquals(0, queue.size());
    }

    @Test
    void shouldScanQueueAndExecuteHalfTheQueue() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final LocalDateTime past = LocalDateTime.now().minusMinutes(random.nextInt(10) + 3);
        final ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        final NLUser user = users.get(0);
        user.setDefaultTemplateId(NLStringValue.of("98djd3sa81"));

        final ELAScheduleMailRequest firstThatShouldBeExecuted = MailModuleUtil.createScheduledMailRequest(user,
                past.toInstant(ZoneOffset.UTC).toEpochMilli(), zoneId.toString()
        );
        final ELAScheduleMailRequest secondThatShouldBeExecuted = MailModuleUtil.createScheduledMailRequest(user,
                past.toInstant(ZoneOffset.UTC).toEpochMilli(), zoneId.toString()
        );
        final ELAScheduleMailRequest thirdThatShouldBeExecuted = MailModuleUtil.createScheduledMailRequest(user,
                past.toInstant(ZoneOffset.UTC).toEpochMilli(), zoneId.toString()
        );

        final LocalDateTime future = LocalDateTime.now().plusMinutes(random.nextInt(10) + 3);
        final ELAScheduleMailRequest firstThatShouldNotBeExecuted = MailModuleUtil.createScheduledMailRequest(user,
                future.toInstant(ZoneOffset.UTC).toEpochMilli(), zoneId.toString()
        );
        final ELAScheduleMailRequest secondThatShouldNotBeExecuted = MailModuleUtil.createScheduledMailRequest(user,
                future.toInstant(ZoneOffset.UTC).toEpochMilli(), zoneId.toString()
        );
        final ELAScheduleMailRequest thirdThatShouldNotBeExecuted = MailModuleUtil.createScheduledMailRequest(user,
                future.toInstant(ZoneOffset.UTC).toEpochMilli(), zoneId.toString()
        );

        executor.schedule(user.map().getId(), ELAScheduleMailDetails.of(firstThatShouldBeExecuted, ZonedDateTime.of(past, zoneId)));
        executor.schedule(user.map().getId(), ELAScheduleMailDetails.of(firstThatShouldNotBeExecuted, ZonedDateTime.of(future, zoneId)));
        executor.schedule(user.map().getId(), ELAScheduleMailDetails.of(secondThatShouldBeExecuted, ZonedDateTime.of(past, zoneId)));
        executor.schedule(user.map().getId(), ELAScheduleMailDetails.of(secondThatShouldNotBeExecuted, ZonedDateTime.of(future, zoneId)));
        executor.schedule(user.map().getId(), ELAScheduleMailDetails.of(thirdThatShouldBeExecuted, ZonedDateTime.of(past, zoneId)));
        executor.schedule(user.map().getId(), ELAScheduleMailDetails.of(thirdThatShouldNotBeExecuted, ZonedDateTime.of(future, zoneId)));
        Assertions.assertEquals(6, queue.size());

        executor.scanQueue();
        Assertions.assertEquals(3, queue.size());
    }

    private void reflectControllerFields() throws NoSuchFieldException, IllegalAccessException {
        final Field schema = requestBuilder.getClass().getDeclaredField("schema");
        final Field homeDomain = requestBuilder.getClass().getDeclaredField("domainName");
        final Field port = requestBuilder.getClass().getDeclaredField("port");
        schema.trySetAccessible();
        homeDomain.trySetAccessible();
        port.trySetAccessible();
        schema.set(requestBuilder, NewslerDesignerServiceProperties.Schema.HTTP);
        homeDomain.set(requestBuilder, "localhost");
        port.setInt(requestBuilder, 4200);
    }
}