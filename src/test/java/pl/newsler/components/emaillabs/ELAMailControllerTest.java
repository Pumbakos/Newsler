package pl.newsler.components.emaillabs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pl.newsler.api.IELAMailController;
import pl.newsler.commons.exception.GlobalRestExceptionHandler;
import pl.newsler.commons.exception.NLError;
import pl.newsler.commons.exception.NLException;
import pl.newsler.commons.model.NLAppKey;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLExecutionDate;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.commons.model.NLUuid;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static pl.newsler.components.emaillabs.MailModuleUtil.createInstantMailRequest;

class ELAMailControllerTest {
    private final GlobalRestExceptionHandler handler = new GlobalRestExceptionHandler();
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubUserRepository userRepository = new StubUserRepository();
    private final StubELAMailRepository mailRepository = new StubELAMailRepository();
    private final IReceiverService receiverService = new StubReceiverModuleConfiguration(new StubReceiverRepository(), userRepository).receiverService();
    private final ELAMailModuleConfiguration configuration = new ELAMailModuleConfiguration(userRepository, mailRepository, passwordEncoder, receiverService);
    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final IELAMailService service = configuration.mailService(
            configuration.taskInstantExecutor(restTemplate, objectMapper),
            configuration.taskScheduledExecutor(restTemplate, objectMapper)
    );
    private final IELAMailController controller = new ELAMailController(service);
    private final TestUserFactory factory = new TestUserFactory();

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
    void shouldQueueMailRequestAndReturn202_AcceptedWhenUserValid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = users.get(0);
        final ELAInstantMailRequest request = createInstantMailRequest(user);

        final ResponseEntity<HttpStatus> response = controller.queueAndExecute(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void shouldNotQueueMailRequestAndReturn400_BadRequestWhenUserInvalid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final ELAInstantMailRequest request = new ELAInstantMailRequest(
                TestUserUtils.email(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
        );

        try {
            controller.queueAndExecute(request);
            Assertions.fail();
        } catch (NLException e) {
            final ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            Assertions.assertTrue(mailRepository.findAll().isEmpty());
        }
    }

    @Test
    void shouldScheduleMailAndReturn202_AcceptedWhenRequestValid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }


        final NLUser user = users.get(0);
        final ELAScheduleMailRequest request = MailModuleUtil.createScheduledMailRequest(
                users,
                user,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(NLExecutionDate.PATTERN)),
                ZoneId.systemDefault().toString()
        );

        final int previousSize = mailRepository.findAll().size();
        final ResponseEntity<HttpStatus> response = controller.schedule(request);
        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        Assertions.assertEquals(previousSize + 1, mailRepository.findAll().size());
    }

    @Test
    void shouldScheduleMailAndReturn202_AcceptedWhenRequestValidButNoZoneFound() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = users.get(0);
        final ELAScheduleMailRequest request = MailModuleUtil.createScheduledMailRequest(
                users,
                user,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(NLExecutionDate.PATTERN)),
                "Zone"
        );

        final int previousSize = mailRepository.findAll().size();
        final ResponseEntity<HttpStatus> response = controller.schedule(request);
        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        Assertions.assertEquals(previousSize + 1, mailRepository.findAll().size());
    }

    @Test
    void shouldNotScheduleMailAndAndReturn400_BadRequestWhenUserNotFound() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = NLDUser.of(users.get(0)).toUser();
        user.setEmail(NLEmail.of(TestUserUtils.email()));

        final ELAScheduleMailRequest request = MailModuleUtil.createScheduledMailRequest(
                users,
                user,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(NLExecutionDate.PATTERN)),
                "Zone"
        );

        final int previousSize = mailRepository.findAll().size();
        try {
            controller.schedule(request);
            Assertions.fail();
        } catch (NLException e) {
            final ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            Assertions.assertEquals(previousSize, mailRepository.findAll().size());
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotScheduleMailAndAndReturn400_BadRequestWhenDateTimeBlank(String dateTime) {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = users.get(0);
        final ELAScheduleMailRequest request = MailModuleUtil.createScheduledMailRequest(
                users,
                user,
                dateTime,
                "Zone"
        );

        final int previousSize = mailRepository.findAll().size();
        try {
            controller.schedule(request);
            Assertions.fail();
        } catch (NLException e) {
            final ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            Assertions.assertEquals(previousSize, mailRepository.findAll().size());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023-02-22T23:54:00", "22-02-2023 23:54:00"})
    void shouldNotScheduleMailAndAndReturn400_BadRequestWhenInvalidDate_DateTimeException(String dateTime) {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = users.get(0);
        final ELAScheduleMailRequest request = MailModuleUtil.createScheduledMailRequest(
                users,
                user,
                dateTime,
                "Zone"
        );

        final int previousSize = mailRepository.findAll().size();
        try {
            controller.schedule(request);
            Assertions.fail();
        } catch (NLException e) {
            final ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            Assertions.assertEquals(previousSize, mailRepository.findAll().size());
        }
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

        controller.queueAndExecute(first);
        controller.queueAndExecute(second);
        controller.queueAndExecute(third);

        final ResponseEntity<List<ELAGetMailResponse>> entity = controller.fetchAllMails(user.map().getId().getValue());
        Assertions.assertNotNull(entity);
        Assertions.assertNotNull(entity.getBody());
        Assertions.assertEquals(mailRepository.findAll().get(0).toResponse(user.getEmail().getValue()), entity.getBody().get(0));
        Assertions.assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    @SuppressWarnings({"java:S5778"})
    void shouldNotFetchAllMailsAndReturn400_BadRequestWhenUserIdInvalid() {
        try {
            controller.fetchAllMails(NLUuid.of(UUID.randomUUID()).getValue());
            Assertions.fail();
        } catch (NLException e) {
            final ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertNotNull(response);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
}