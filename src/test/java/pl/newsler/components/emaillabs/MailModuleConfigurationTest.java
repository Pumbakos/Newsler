package pl.newsler.components.emaillabs;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmailStatus;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLIdType;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.components.emaillabs.dto.ELASendMailResponse;
import pl.newsler.components.emaillabs.dto.MailSendRequest;
import pl.newsler.components.emaillabs.exceptions.ELAMailNotFoundException;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.components.user.UserDataNotFineException;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
class MailModuleConfigurationTest {
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubUserRepository userRepository = new StubUserRepository();
    private final StubMailRepository mailRepository = new StubMailRepository();
    private final MailModuleConfiguration configuration = new MailModuleConfiguration(userRepository, mailRepository, passwordEncoder);
    private final TestUserFactory factory = new TestUserFactory();
    private final Gson gson = new Gson();
    private MockRestServiceServer mockServer;
    private RestTemplate restTemplate;
    private IMailService service;

    @BeforeEach
    void beforeEach() {
        restTemplate = Mockito.mock(RestTemplate.class);
        service = configuration.mailService(configuration.taskExecutor(restTemplate));
        mockServer = MockRestServiceServer.createServer(restTemplate);

        NLId standardId = NLId.of(UUID.randomUUID());
        factory.standard().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.standard_plainPassword())));
        factory.standard().setId(standardId);
        factory.standard().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.standard().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.standard().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        userRepository.save(factory.standard());

        NLId dashedId = NLId.of(UUID.randomUUID());
        factory.dashed().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dashed_plainPassword())));
        factory.dashed().setId(dashedId);
        factory.dashed().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dashed().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dashed().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        userRepository.save(factory.dashed());

        NLId dottedId = NLId.of(UUID.randomUUID());
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
    void shouldNotGetMailStatus_UserIdValid_MailIdInvalid() {
        NLId userId = factory.dashed().map().getId();
        NLId userTypeId = NLId.of(UUID.randomUUID());
        Assertions.assertThrows(ELAMailNotFoundException.class, () -> service.getMailStatus(userTypeId, userId));

        NLId mailTypeId = NLId.of(UUID.randomUUID(), NLIdType.MAIL);
        Assertions.assertThrows(ELAMailNotFoundException.class, () -> service.getMailStatus(mailTypeId, userId));
    }

    @Test
    void shouldNotGetMailStatus_UserIdInvalid() {
        NLId id = NLId.of(UUID.randomUUID());
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
        MailSendRequest request = new MailSendRequest(
                user.getEmail().getValue(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                List.of(""),
                List.of(""),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
        );

        final AtomicReference<List<NLUserMail>> mails = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> service.queue(request));
        Assertions.assertDoesNotThrow(() -> mails.set(service.fetchAllMails(user.map().getId())));
        NLUserMail mail = mails.get().get(0);
        Assertions.assertEquals(NLEmailStatus.QUEUED, mail.getStatus());
        Assertions.assertDoesNotThrow(() -> service.getMailStatus(mail.getId(), user.map().getId()));
    }

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
        List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        ELASendMailResponse response = ELASendMailResponse.of(200, "OK", "", "", TestUtils.reqId());

        mockServer.expect(MockRestRequestMatchers.requestTo("/v1/api/mails")).andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(gson.toJson(response), MediaType.APPLICATION_JSON));

        NLUser user = users.get(0);
        MailSendRequest request = new MailSendRequest(
                user.getEmail().getValue(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                List.of(""),
                List.of(""),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
        );
        Assertions.assertDoesNotThrow(() -> service.queue(request));
    }

    @Test
    void shouldQueueTwoMailSExecutionAndExecuteIt() {
        List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        ELASendMailResponse response = ELASendMailResponse.of(200, "OK", "", "", TestUtils.reqId());

        mockServer.expect(MockRestRequestMatchers.requestTo("/v1/api/mails")).andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(gson.toJson(response), MediaType.APPLICATION_JSON));

        NLUser user = users.get(0);
        MailSendRequest request = new MailSendRequest(
                user.getEmail().getValue(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                List.of(""),
                List.of(""),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
        );
        Assertions.assertDoesNotThrow(() -> service.queue(request));
        Assertions.assertDoesNotThrow(() -> service.queue(request));
        Assertions.assertEquals(2, mailRepository.findAll().size());
    }

    @Test
    @Disabled
    void shouldQueueMailExecutionAndExecuteItButThrowRestClientException_ClientExceptionUnauthorized() {
        List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }

        NLUser user = users.get(0);
        MailSendRequest request = new MailSendRequest(
                user.getEmail().getValue(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                List.of(""),
                List.of(""),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
        );

        MailDetails details = MailDetails.of(request);
        final Map<String, String> params = new LinkedHashMap<>();

        String name = String.format("%s %s", user.getFirstName(), user.getLastName());
        params.put(Param.FROM, user.getEmail().getValue());
        params.put(Param.FROM_NAME, name);
        params.put(Param.SMTP_ACCOUNT, passwordEncoder.decrypt(user.getSmtpAccount().getValue()));
        params.put(String.format(Param.TO_ADDRESS_NAME, user.getEmail().getValue(), name), Arrays.toString(details.toAddresses().toArray()));
        params.put(Param.SUBJECT, details.subject());
        params.put(Param.HTML, String.format("<pre>%s</pre>", details.message()));
        params.put(Param.TEXT, details.message());

        HttpEntity<String> entity = new HttpEntity<>(ELAUrlParamBuilder.build(params));
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl("https://localhost:8443")
                .path("/v1/api/mails")
                .build();

        Mockito.when(restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, entity, String.class))
                .thenReturn(new ResponseEntity<>("MOCK RESPONSE", HttpStatus.UNAUTHORIZED));

        mockServer
                .expect(MockRestRequestMatchers.requestTo("/v1/api/mails"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertDoesNotThrow(() -> service.queue(request));
        Assertions.assertEquals(NLEmailStatus.ERROR, mailRepository.findAll().get(0).getStatus());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotBuildParams(Map<String, String> map) {
        String params = ELAUrlParamBuilder.build(map);
        Assertions.assertEquals("", params);
    }

    @Test
    void shouldBuildParams() {
        List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }
        NLUser user = users.get(0);
        MailSendRequest request = new MailSendRequest(
                user.getEmail().getValue(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                List.of(""),
                List.of(""),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
        );
        MailDetails details = MailDetails.of(request);

        final Map<String, String> params = new LinkedHashMap<>();

        String name = String.format("%s %s", user.getFirstName(), user.getLastName());
        params.put(Param.FROM, user.getEmail().getValue());
        params.put(Param.FROM_NAME, name);
        params.put(Param.SMTP_ACCOUNT, passwordEncoder.decrypt(user.getSmtpAccount().getValue()));
        params.put(String.format(Param.TO_ADDRESS_NAME, user.getEmail().getValue(), name), Arrays.toString(details.toAddresses().toArray()));
        params.put(Param.SUBJECT, details.subject());
        params.put(Param.HTML, String.format("<pre>%s</pre>", details.message()));
        params.put(Param.TEXT, details.message());

        String paramsWithUrlEncoded = ELAUrlParamBuilder.build(params);
        String paramsWithoutURLEncoding = buildWithoutURLEncoding(params);
        String decodedParamsWithUrlEncoded = Arrays.stream(paramsWithUrlEncoded.split("&"))
                .map(param -> param.split("=")[0] + "=" + decode(param.split("=")[1]))
                .collect(Collectors.joining("&"));
        String decodedParamsWithUrlEncodedAndSpecialChars = decodeSpecialChars(decodedParamsWithUrlEncoded);
        Assertions.assertNotNull(params);
        Assertions.assertNotNull(paramsWithUrlEncoded);
        Assertions.assertEquals(paramsWithoutURLEncoding, decodedParamsWithUrlEncodedAndSpecialChars);
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

    String decode(String value) {
        return decodeSpecialChars(URLDecoder.decode(value, StandardCharsets.UTF_8));
    }

    String decodeSpecialChars(String value) {
        return value
                .replace("%5D", "]")
                .replace("%5B", "[")
                .replace("%40", "@")
                .replace("+", " ");
    }
}