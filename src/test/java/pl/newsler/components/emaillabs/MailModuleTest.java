package pl.newsler.components.emaillabs;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import pl.newsler.api.IMailController;
import pl.newsler.api.exceptions.GlobalRestExceptionHandler;
import pl.newsler.commons.exceptions.NLError;
import pl.newsler.commons.exceptions.NLException;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmailStatus;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLIdType;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.components.emaillabs.dto.ELASendMailResponse;
import pl.newsler.components.emaillabs.dto.GetMailStatus;
import pl.newsler.components.emaillabs.dto.MailSendRequest;
import pl.newsler.components.emaillabs.exceptions.ELAMailNotFoundException;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.api.exceptions.UserDataNotFineException;
import pl.newsler.security.StubNLPasswordEncoder;
import pl.newsler.testcommons.TestUserUtils;
import pl.newsler.testcommons.TestUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
class MailModuleTest {
    private final GlobalRestExceptionHandler handler = new GlobalRestExceptionHandler();
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubUserRepository userRepository = new StubUserRepository();
    private final StubMailRepository mailRepository = new StubMailRepository();
    private final MailModuleConfiguration configuration = new MailModuleConfiguration(userRepository, mailRepository, passwordEncoder);
    private final TestUserFactory factory = new TestUserFactory();
    private final Gson gson = new Gson();
    private MockRestServiceServer mockServer;
    private IMailService service;
    private IMailController controller;

    @BeforeEach
    void beforeEach() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        service = configuration.mailService(configuration.taskExecutor(restTemplate));
        mockServer = MockRestServiceServer.createServer(restTemplate);
        controller = new MailController(service);

        NLUuid standardId = NLUuid.of(UUID.randomUUID());
        factory.standard().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.standard_plainPassword())));
        factory.standard().setId(standardId);
        factory.standard().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.standard().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.standard().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        userRepository.save(factory.standard());

        NLUuid dashedId = NLUuid.of(UUID.randomUUID());
        factory.dashed().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dashed_plainPassword())));
        factory.dashed().setId(dashedId);
        factory.dashed().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dashed().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dashed().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        userRepository.save(factory.dashed());

        NLUuid dottedId = NLUuid.of(UUID.randomUUID());
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


    /* --------------- CONTROLLER ----------------- */
    /* ------------------ QUEUE ------------------- */
    @Test
    void shouldQueueMailRequestAndReturn202_AcceptedWhenUserValid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = users.get(0);
        final MailSendRequest request = createMailRequest(users, user);

        ResponseEntity<HttpStatus> response = controller.queue(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void shouldNotQueueMailRequestAndReturn400_BadRequestWhenUserInvalid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final MailSendRequest request = new MailSendRequest(
                TestUserUtils.email(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                List.of(""),
                List.of(""),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
        );

        try {
            controller.queue(request);
            Assertions.fail();
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            Assertions.assertTrue(mailRepository.findAll().isEmpty());
        }
    }

    /* -------------- FETCH ALL MAILS ------------- */
    @Test
    void shouldFetchAllMailsAndReturn200_OkWhenUserIdValid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = users.get(0);
        final MailSendRequest first = createMailRequest(users, user);
        final MailSendRequest second = createMailRequest(users, user);
        final MailSendRequest third = createMailRequest(users, user);

        controller.queue(first);
        controller.queue(second);
        controller.queue(third);

        ResponseEntity<List<NLUserMail>> responseEntity = controller.fetchAllMails(user.map().getId().getValue());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(mailRepository.findAll(), responseEntity.getBody());
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @SuppressWarnings({"java:S5778"})
    void shouldNotFetchAllMailsAndReturn400_BadRequestWhenUserIdInvalid() {
        try {
            controller.fetchAllMails(NLUuid.of(UUID.randomUUID()).getValue());
            Assertions.fail();
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertNotNull(response);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    /* ----------------- GET MAILS ---------------- */
    @Test
    void shouldGetMailStatusAndReturn200_OKWhenMailIdAndUserIdAreValid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final NLUser user = users.get(0);
        final MailSendRequest first = createMailRequest(users, user);
        final MailSendRequest second = createMailRequest(users, user);
        final MailSendRequest third = createMailRequest(users, user);

        controller.queue(first);
        controller.queue(second);
        controller.queue(third);

        NLUserMail mail = mailRepository.findAll().get(0);
        ResponseEntity<GetMailStatus> response = controller.getMailStatus(mail.getId().getValue(), mail.getUserId().getValue());
        Assertions.assertNotNull(response);
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).error());
        Assertions.assertEquals(mail.getStatus(), response.getBody().status());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldNotGetMailStatusAndReturn400_BadRequestWhenMailIdOrUserIdInvalidated() {
        try {
            controller.getMailStatus("a187-0d-43-02-0f7f2b", "abb8dc3b-73ec-4b31-bab7-9b7d25892b4c");
            Assertions.fail();
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertNotNull(response);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        try {
            controller.getMailStatus("abb8dc3b-73ec-4b31-bab7-9b7d25892b4c", "a187-0d-43-02-0f7f2b");
            Assertions.fail();
        } catch (NLException e) {
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertNotNull(response);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    /* ------------------ SERVICE ----------------- */
    /* ------------- GET & FETCH MAILS ------------ */
    @Test
    void shouldNotGetMailStatus_UserIdValid_MailIdInvalid() {
        final NLUuid userId = factory.dashed().map().getId();
        final NLUuid userTypeId = NLUuid.of(UUID.randomUUID());
        Assertions.assertThrows(ELAMailNotFoundException.class, () -> service.getMailStatus(userTypeId, userId));

        final NLUuid mailTypeId = NLUuid.of(UUID.randomUUID(), NLIdType.MAIL);
        Assertions.assertThrows(ELAMailNotFoundException.class, () -> service.getMailStatus(mailTypeId, userId));
    }

    @Test
    void shouldNotGetMailStatus_UserIdInvalid() {
        final NLUuid id = NLUuid.of(UUID.randomUUID());
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.getMailStatus(id, id));
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.getMailStatus(id, id));
    }

    @Test
    void shouldGetMailStatus_UserIdValid_MailIdValid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final ELASendMailResponse response = ELASendMailResponse.of(200, "OK", "", "", TestUtils.reqId());
        mockServer.expect(MockRestRequestMatchers.requestTo("/v1/api/mails")).andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(gson.toJson(response), MediaType.APPLICATION_JSON));

        final NLUser user = users.get(0);
        final MailSendRequest request = createMailRequest(users, user);

        final AtomicReference<List<NLUserMail>> mails = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> service.queue(request));
        Assertions.assertDoesNotThrow(() -> mails.set(service.fetchAllMails(user.map().getId())));
        NLUserMail mail = mails.get().get(0);
        Assertions.assertEquals(NLEmailStatus.QUEUED, mail.getStatus());
        Assertions.assertDoesNotThrow(() -> service.getMailStatus(mail.getId(), user.map().getId()));
    }

    /* ---------------- QUEUE MAIL ---------------- */
    @Test
    void shouldNotQueueMail_RequesterEmailInvalid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final ELASendMailResponse response = ELASendMailResponse.of(200, "OK", "", "", TestUtils.reqId());
        mockServer.expect(MockRestRequestMatchers.requestTo("/v1/api/mails")).andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(gson.toJson(response), MediaType.APPLICATION_JSON));

        final MailSendRequest request = new MailSendRequest(
                TestUserUtils.email(),
                List.of(users.get(0).getEmail().getValue(), users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                List.of(""),
                List.of(""),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
        );

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.queue(request));
    }

    @Test
    void shouldQueueMailExecutionAndExecuteIt() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final ELASendMailResponse response = ELASendMailResponse.of(200, "OK", "", "", TestUtils.reqId());
        mockServer.expect(MockRestRequestMatchers.requestTo("/v1/api/mails")).andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(gson.toJson(response), MediaType.APPLICATION_JSON));

        final NLUser user = users.get(0);
        final MailSendRequest request = createMailRequest(users, user);
        Assertions.assertDoesNotThrow(() -> service.queue(request));
    }

    @Test
    void shouldQueueTwoMailsExecutionAndExecuteIt() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final ELASendMailResponse response = ELASendMailResponse.of(200, "OK", "", "", TestUtils.reqId());
        mockServer.expect(MockRestRequestMatchers.requestTo("/v1/api/mails")).andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(gson.toJson(response), MediaType.APPLICATION_JSON));

        final NLUser user = users.get(0);
        final MailSendRequest request = createMailRequest(users, user);
        Assertions.assertDoesNotThrow(() -> service.queue(request));
        Assertions.assertDoesNotThrow(() -> service.queue(request));
        Assertions.assertEquals(2, mailRepository.findAll().size());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotBuildParams(Map<String, String> map) {
        String params = ELAUrlParamBuilder.build(map);
        Assertions.assertEquals("", params);
    }

    /* ------------- ELAParamBuilder -------------- */
    @Test
    void shouldBuildParams() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }
        final NLUser user = users.get(0);
        final MailSendRequest request = createMailRequest(users, user);
        final MailDetails details = MailDetails.of(request);
        final Map<String, String> params = new LinkedHashMap<>();
        final String name = String.format("%s %s", user.getFirstName(), user.getLastName());

        params.put(Param.FROM, user.getEmail().getValue());
        params.put(Param.FROM_NAME, name);
        params.put(Param.SMTP_ACCOUNT, passwordEncoder.decrypt(user.getSmtpAccount().getValue()));
        params.put(String.format(Param.TO_ADDRESS_NAME, user.getEmail().getValue(), name), Arrays.toString(details.toAddresses().toArray()));
        params.put(Param.SUBJECT, details.subject());
        params.put(Param.HTML, String.format("<pre>%s</pre>", details.message()));
        params.put(Param.TEXT, details.message());

        final String paramsWithUrlEncoded = ELAUrlParamBuilder.build(params);
        final String paramsWithoutURLEncoding = buildWithoutURLEncoding(params);
        final String decodedParamsWithUrlEncoded = Arrays
                .stream(paramsWithUrlEncoded.split("&"))
                .map(param -> param.split("=")[0] + "=" + decode(param.split("=")[1]))
                .collect(Collectors.joining("&"));
        final String decodedParamsWithUrlEncodedAndSpecialChars = decodeSpecialChars(decodedParamsWithUrlEncoded);
        Assertions.assertNotNull(params);
        Assertions.assertNotNull(paramsWithUrlEncoded);
        Assertions.assertEquals(paramsWithoutURLEncoding, decodedParamsWithUrlEncodedAndSpecialChars);
    }

    /* --------------- NLUserMail ----------------- */
    @Test
    @SuppressWarnings({"java:S5863"})
    void shouldCompareNLUserMail() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }
        final NLUser user = users.get(0);
        final MailSendRequest requestForFirst = createMailRequest(users, user);
        final MailSendRequest requestForSecond = new MailSendRequest(
                user.getEmail().getValue(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                null,
                null,
                "MOCK TEST",
                "MOCK TEST MESSAGE"
        );
        final NLUserMail first = NLUserMail.of(NLUuid.of(UUID.randomUUID(), NLIdType.MAIL), MailDetails.of(requestForFirst));
        final NLUserMail second = NLUserMail.of(NLUuid.of(UUID.randomUUID(), NLIdType.MAIL), MailDetails.of(requestForSecond));

        Assertions.assertEquals(first, first);
        Assertions.assertEquals(first.toString(), first.toString());
        Assertions.assertEquals(first.hashCode(), first.hashCode());
        Assertions.assertNotEquals(first, second);
        Assertions.assertNotEquals(first.toString(), second.toString());
        Assertions.assertNotEquals(first.hashCode(), second.hashCode());
        Assertions.assertNotEquals(first, second);
    }

    @Test
    void shouldGetNLUserMailProperties() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }
        final NLUser user = users.get(0);
        final MailSendRequest request = createMailRequest(users, user);
        final NLUserMail first = NLUserMail.of(NLUuid.of(UUID.randomUUID(), NLIdType.MAIL), MailDetails.of(request));

        Assertions.assertNotNull(first);
        Assertions.assertNotNull(first.getId());
        Assertions.assertNotNull(first.getUserId());
        Assertions.assertNotNull(first.getBcc());
        Assertions.assertNotNull(first.getCc());
        Assertions.assertNotNull(first.getMessage());
        Assertions.assertNotNull(first.getStatus());
        Assertions.assertNotNull(first.getToAddresses());
        Assertions.assertNotNull(first.getVersion());
        Assertions.assertTrue(first.getErrorMessage().getValue().isEmpty());
    }

    static String buildWithoutURLEncoding(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }

        final StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append("&");
            }
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
        }

        return builder.toString();
    }

    @NotNull
    private static String decode(String value) {
        return decodeSpecialChars(URLDecoder.decode(value, StandardCharsets.UTF_8));
    }

    @NotNull
    private static String decodeSpecialChars(String value) {
        return value
                .replace("%5D", "]")
                .replace("%5B", "[")
                .replace("%40", "@")
                .replace("+", " ");
    }

    @NotNull
    private static MailSendRequest createMailRequest(List<NLUser> users, NLUser user) {
        return new MailSendRequest(
                user.getEmail().getValue(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                List.of(""),
                List.of(""),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
        );
    }
}