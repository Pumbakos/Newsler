package pl.newsler.components.emaillabs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.model.NLAppKey;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.components.emaillabs.exception.ELATemplateDeletionException;
import pl.newsler.components.emaillabs.exception.ELAValidationRequestException;
import pl.newsler.components.emaillabs.executor.ELARequestBuilder;
import pl.newsler.components.emaillabs.usecase.ELATemplateAddResponse;
import pl.newsler.components.user.IUserCrudService;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.StubUserModuleConfiguration;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.security.NLIPasswordEncoder;
import pl.newsler.security.StubNLPasswordEncoder;

import java.net.URI;
import java.net.URISyntaxException;

import static pl.newsler.testcommons.TestUserUtils.secretOrAppKey;
import static pl.newsler.testcommons.TestUserUtils.smtpAccount;

class ELATemplateServiceTest {
    private final NLIPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubUserRepository userRepository = new StubUserRepository();
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final ELATemplateService service = new ELATemplateService(new ELARequestBuilder(passwordEncoder), restTemplate);
    private final StubUserModuleConfiguration configuration = new StubUserModuleConfiguration(
            userRepository,
            passwordEncoder,
            service
    );
    private final IUserCrudService userCrudService = configuration.userService();
    private final TestUserFactory factory = new TestUserFactory();

    @BeforeEach
    void beforeEach() {
        factory.standard().setUuid(
                userCrudService.create(
                        NLFirstName.of(factory.standard().getFirstName().getValue()),
                        NLLastName.of(factory.standard().getLastName().getValue()),
                        NLEmail.of(factory.standard().getEmail().getValue()),
                        NLPassword.of(factory.standard().getNLPassword().getValue())
                ));
        factory.dashed().setUuid(
                userCrudService.create(
                        NLFirstName.of(factory.dashed().getFirstName().getValue()),
                        NLLastName.of(factory.dashed().getLastName().getValue()),
                        NLEmail.of(factory.dashed().getEmail().getValue()),
                        NLPassword.of(factory.dashed().getNLPassword().getValue())
                ));
        factory.dotted().setUuid(
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
    void shouldAddTemplateForUserWhenELADetailsValid() throws URISyntaxException, JsonProcessingException {
        final NLUser user = factory.standard();
        setElaDetails(user);

        final ELATemplateAddResponse response = createTemplateAddResponse(200);

        final String endpoint = ELARequestPoint.BASE_URL.concat(ELARequestPoint.ADD_TEMPLATE_URL);
        Mockito.when(restTemplate.exchange(
                        ArgumentMatchers.eq(new URI(endpoint)),
                        ArgumentMatchers.eq(HttpMethod.POST),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.<Class<String>>any()
                )
        ).thenReturn(new ResponseEntity<>(mapper.writeValueAsString(response), HttpStatus.OK));

        final String templateId = service.add(user, "<pre>TEMPLATE<pre>", "TEMPLATE");
        Assertions.assertEquals(response.getTemplateId(), templateId);
    }

    @Test
    void shouldValidateWhetherToAddTemplateOrNot() throws URISyntaxException, JsonProcessingException {
        final NLUser user = factory.standard();
        setElaDetails(user);

        final ELATemplateAddResponse response = createTemplateAddResponse(200);

        final String endpoint = ELARequestPoint.BASE_URL.concat(ELARequestPoint.ADD_TEMPLATE_URL);
        Mockito.when(restTemplate.exchange(
                        ArgumentMatchers.eq(new URI(endpoint)),
                        ArgumentMatchers.eq(HttpMethod.POST),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.<Class<String>>any()
                )
        ).thenReturn(new ResponseEntity<>(mapper.writeValueAsString(response), HttpStatus.OK));

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.add(user, "", ""));
        Assertions.assertDoesNotThrow(() -> service.add(user, "HTML", ""));
        Assertions.assertDoesNotThrow(() -> service.add(user, "", "TEXT"));
    }

    @Test
    void shouldNotAddTemplateForUserAndThrowInvalidUserDataExceptionWhenELADetailsInvalid() throws URISyntaxException, JsonProcessingException {
        final NLUser user = factory.standard();
        setElaDetails(user);

        final ELATemplateAddResponse response = createTemplateAddResponse(401);

        final String endpoint = ELARequestPoint.BASE_URL.concat(ELARequestPoint.ADD_TEMPLATE_URL);
        Mockito.when(restTemplate.exchange(
                        ArgumentMatchers.eq(new URI(endpoint)),
                        ArgumentMatchers.eq(HttpMethod.POST),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.<Class<String>>any()
                )
        ).thenReturn(new ResponseEntity<>(mapper.writeValueAsString(response), HttpStatus.UNAUTHORIZED));

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.add(user, "<pre>TEMPLATE<pre>", "TEMPLATE"));
    }

    @Test
    void shouldNotAddTemplateForUserAndThrowInvalidUserDataExceptionWhenToManyTemplates() throws URISyntaxException, JsonProcessingException {
        final NLUser user = factory.standard();
        setElaDetails(user);

        final ELATemplateAddResponse response = createTemplateAddResponse(500);

        final String endpoint = ELARequestPoint.BASE_URL.concat(ELARequestPoint.ADD_TEMPLATE_URL);
        Mockito.when(restTemplate.exchange(
                        ArgumentMatchers.eq(new URI(endpoint)),
                        ArgumentMatchers.eq(HttpMethod.POST),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.<Class<String>>any()
                )
        ).thenReturn(new ResponseEntity<>(mapper.writeValueAsString(response), HttpStatus.INTERNAL_SERVER_ERROR));

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.add(user, "<pre>TEMPLATE<pre>", "TEMPLATE"));
    }

    @Test
    void shouldNotAddTemplateForUserWhenErrorOccurs() throws URISyntaxException {
        final NLUser user = factory.standard();
        setElaDetails(user);

        final String endpoint = ELARequestPoint.BASE_URL.concat(ELARequestPoint.ADD_TEMPLATE_URL);
        Mockito.when(restTemplate.exchange(
                        ArgumentMatchers.eq(new URI(endpoint)),
                        ArgumentMatchers.eq(HttpMethod.POST),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.<Class<String>>any()
                )
        ).thenThrow(RestClientException.class);

        Assertions.assertThrows(ELAValidationRequestException.class, () -> service.add(user, "<pre>TEMPLATE<pre>", "TEMPLATE"));
    }

    @Test
    void shouldDeleteTemplateWhenTemplateIdAndElaDetailsValid() throws URISyntaxException {
        final NLUser user = factory.standard();
        setElaDetails(user);

        final String endpoint = ELARequestPoint.BASE_URL.concat(ELARequestPoint.DELETE_TEMPLATE_URL.replace("{templateId}", "26a1260f"));
        Mockito.when(restTemplate.exchange(
                        ArgumentMatchers.eq(new URI(endpoint)),
                        ArgumentMatchers.eq(HttpMethod.DELETE),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.<Class<String>>any()
                )
        ).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        Assertions.assertDoesNotThrow(() -> service.remove(user, "26a1260f"));
    }

    @Test
    void shouldNotDeleteTemplateAndThrowInvalidUserDataExceptionWhenElaDetailsInvalid() throws URISyntaxException {
        final NLUser user = factory.standard();
        setElaDetails(user);

        final String endpoint = ELARequestPoint.BASE_URL.concat(ELARequestPoint.DELETE_TEMPLATE_URL.replace("{templateId}", "26a1260f"));
        Mockito.when(restTemplate.exchange(
                        ArgumentMatchers.eq(new URI(endpoint)),
                        ArgumentMatchers.eq(HttpMethod.DELETE),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.<Class<String>>any()
                )
        ).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.remove(user, "26a1260f"));
    }

    @Test
    void shouldNotDeleteTemplateAndThorwInvalidUserDataExceptionWhenTemplateNotFound() throws URISyntaxException {
        final NLUser user = factory.standard();
        setElaDetails(user);

        final String endpoint = ELARequestPoint.BASE_URL.concat(ELARequestPoint.DELETE_TEMPLATE_URL.replace("{templateId}", "26a1260f"));
        Mockito.when(restTemplate.exchange(
                        ArgumentMatchers.eq(new URI(endpoint)),
                        ArgumentMatchers.eq(HttpMethod.DELETE),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.<Class<String>>any()
                )
        ).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        Assertions.assertThrows(InvalidUserDataException.class, () -> service.remove(user, "26a1260f"));
    }

    @Test
    void shouldNotDeleteUserTemplateWhenErrorOccurs() throws URISyntaxException {
        final NLUser user = factory.standard();
        setElaDetails(user);

        final String endpoint = ELARequestPoint.BASE_URL.concat(ELARequestPoint.DELETE_TEMPLATE_URL.replace("{templateId}", "26a1260f"));
        Mockito.when(restTemplate.exchange(
                        ArgumentMatchers.eq(new URI(endpoint)),
                        ArgumentMatchers.eq(HttpMethod.DELETE),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.<Class<String>>any()
                )
        ).thenThrow(RestClientException.class);

        Assertions.assertThrows(ELATemplateDeletionException.class, () -> service.remove(user, "26a1260f"));
    }

    private void setElaDetails(final NLUser user) {
        user.setAppKey(NLAppKey.of(passwordEncoder.encrypt(secretOrAppKey())));
        user.setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(secretOrAppKey())));
        user.setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(smtpAccount())));
    }

    @NotNull
    private static ELATemplateAddResponse createTemplateAddResponse(final int code) {
        final ELATemplateAddResponse response = new ELATemplateAddResponse();
        response.setCode(code);
        response.setData(new ELATemplateAddResponse.Data("26a1260f"));
        response.setMessage("Template saved");
        response.setStatus("success");
        response.setReqId("bd2efe1f");
        return response;
    }
}