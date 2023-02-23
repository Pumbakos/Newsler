package pl.newsler.components.emaillabs;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import pl.newsler.api.IELAMailController;
import pl.newsler.commons.exception.GlobalRestExceptionHandler;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.exception.NLError;
import pl.newsler.commons.exception.NLException;
import pl.newsler.commons.model.NLAppKey;
import pl.newsler.commons.model.NLIdType;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.executor.ELAInstantMailDetails;
import pl.newsler.components.emaillabs.executor.ELAParamBuilder;
import pl.newsler.components.emaillabs.usecase.ELAGetMailResponse;
import pl.newsler.components.emaillabs.usecase.ELAMailSendRequest;
import pl.newsler.components.emaillabs.usecase.ELASendMailResponse;
import pl.newsler.components.receiver.IReceiverService;
import pl.newsler.components.receiver.StubReceiverModuleConfiguration;
import pl.newsler.components.receiver.StubReceiverRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.security.StubNLPasswordEncoder;
import pl.newsler.testcommons.TestUserUtils;
import pl.newsler.testcommons.TestUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
class MailModuleTest {
    private final GlobalRestExceptionHandler handler = new GlobalRestExceptionHandler();
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubUserRepository userRepository = new StubUserRepository();
    private final StubELAMailRepository mailRepository = new StubELAMailRepository();
    private final IReceiverService receiverRepository = new StubReceiverModuleConfiguration(new StubReceiverRepository(), userRepository).receiverService();
    private final ELAMailModuleConfiguration configuration = new ELAMailModuleConfiguration(userRepository, mailRepository, passwordEncoder, receiverRepository);
    private final TestUserFactory factory = new TestUserFactory();
    private final Gson gson = new Gson();
    private MockRestServiceServer mockServer;
    private IELAMailService service;
    private IELAMailController controller;

    @BeforeEach
    void beforeEach() {
        final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        final ObjectMapper objectMapper = new ObjectMapper();
        service = configuration.mailService(configuration.taskInstantExecutor(restTemplate, objectMapper), configuration.taskScheduledExecutor(restTemplate, objectMapper));
        mockServer = MockRestServiceServer.createServer(restTemplate);
        controller = new ELAMailController(service);

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
        final ELAMailSendRequest request = createMailRequest(users, user);

        ResponseEntity<HttpStatus> response = controller.queueAndExecute(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void shouldNotQueueMailRequestAndReturn400_BadRequestWhenUserInvalid() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        final ELAMailSendRequest request = new ELAMailSendRequest(
                TestUserUtils.email(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                List.of(""),
                List.of(""),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
                );

        try {
            controller.queueAndExecute(request);
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
        final ELAMailSendRequest first = createMailRequest(users, user);
        final ELAMailSendRequest second = createMailRequest(users, user);
        final ELAMailSendRequest third = createMailRequest(users, user);

        controller.queueAndExecute(first);
        controller.queueAndExecute(second);
        controller.queueAndExecute(third);

        ResponseEntity<List<ELAGetMailResponse>> entity = controller.fetchAllMails(user.map().getId().getValue());
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
            ResponseEntity<NLError> response = handler.handleException(e);
            Assertions.assertNotNull(response);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    /* ------------------ SERVICE ----------------- */
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

        final ELAMailSendRequest request = new ELAMailSendRequest(
                TestUserUtils.email(),
                List.of(users.get(0).getEmail().getValue(), users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                List.of(""),
                List.of(""),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
                );

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.queue(request));
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
        final ELAMailSendRequest request = createMailRequest(users, user);
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
        final ELAMailSendRequest request = createMailRequest(users, user);
        Assertions.assertDoesNotThrow(() -> service.queue(request));
        Assertions.assertDoesNotThrow(() -> service.queue(request));
        Assertions.assertEquals(2, mailRepository.findAll().size());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotBuildParams(Map<String, String> map) {
        String params = ELAParamBuilder.buildUrlEncoded(map);
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
        final ELAMailSendRequest request = createMailRequest(users, user);
        final ELAInstantMailDetails details = ELAInstantMailDetails.of(request);
        final Map<String, String> params = new LinkedHashMap<>();
        final String name = String.format("%s %s", user.getFirstName(), user.getLastName());

        params.put(ELAParam.FROM, user.getEmail().getValue());
        params.put(ELAParam.FROM_NAME, name);
        params.put(ELAParam.SMTP_ACCOUNT, passwordEncoder.decrypt(user.getSmtpAccount().getValue()));
        params.put(String.format(ELAParam.TO, user.getEmail().getValue(), name), Arrays.toString(details.toAddresses().toArray()));
        params.put(ELAParam.SUBJECT, details.subject());
        params.put(ELAParam.HTML, String.format("<pre>%s</pre>", details.message()));
        params.put(ELAParam.TEXT, details.message());

        final String paramsWithUrlEncoded = ELAParamBuilder.buildUrlEncoded(params);
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
        final ELAMailSendRequest requestForFirst = createMailRequest(users, user);
        final ELAMailSendRequest requestForSecond = new ELAMailSendRequest(
                user.getEmail().getValue(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                null,
                null,
                "MOCK TEST",
                "MOCK TEST MESSAGE"
                );
        final ELAUserMail first = ELAUserMail.of(NLUuid.of(UUID.randomUUID(), NLIdType.MAIL), ELAInstantMailDetails.of(requestForFirst));
        final ELAUserMail second = ELAUserMail.of(NLUuid.of(UUID.randomUUID(), NLIdType.MAIL), ELAInstantMailDetails.of(requestForSecond));

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
        final ELAMailSendRequest request = createMailRequest(users, user);
        final ELAUserMail first = ELAUserMail.of(NLUuid.of(UUID.randomUUID(), NLIdType.MAIL), ELAInstantMailDetails.of(request));

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
    private static ELAMailSendRequest createMailRequest(List<NLUser> users, NLUser user) {
        return new ELAMailSendRequest(
                user.getEmail().getValue(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                List.of(""),
                List.of(""),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
                );
    }
}