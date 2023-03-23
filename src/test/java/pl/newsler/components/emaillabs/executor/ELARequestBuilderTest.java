package pl.newsler.components.emaillabs.executor;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import pl.newsler.commons.model.NLAppKey;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.commons.model.NLStringValue;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.ELAParam;
import pl.newsler.components.emaillabs.MailModuleUtil;
import pl.newsler.components.emaillabs.exception.ELAParameterBuildException;
import pl.newsler.components.emaillabs.usecase.ELAInstantMailRequest;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.internal.NewslerDesignerServiceProperties;
import pl.newsler.security.StubNLPasswordEncoder;
import pl.newsler.testcommons.TestUserUtils;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

class ELARequestBuilderTest {
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubUserRepository userRepository = new StubUserRepository();
    private final TestUserFactory factory = new TestUserFactory();
    private final ELARequestBuilder paramBuilder = new ELARequestBuilder(passwordEncoder);

    @BeforeEach
    void beforeEach() throws NoSuchFieldException, IllegalAccessException {
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

        Field schema = paramBuilder.getClass().getDeclaredField("schema");
        Field domain = paramBuilder.getClass().getDeclaredField("domainName");
        Field port = paramBuilder.getClass().getDeclaredField("port");
        schema.trySetAccessible();
        domain.trySetAccessible();
        port.trySetAccessible();
        schema.set(paramBuilder, NewslerDesignerServiceProperties.Schema.HTTP);
        domain.set(paramBuilder, "localhost");
        port.setInt(paramBuilder, 4200);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void shouldBuildParams() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }
        final NLUser user = users.get(0);
        final ELAInstantMailRequest request = MailModuleUtil.createInstantMailRequest(user);
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

        final String paramsWithUrlEncoded = paramBuilder.buildUrlEncoded(params);
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

    @Test
    @Disabled("Due to lack of idea")
    void shouldAppendUnsubscribeFooterAtTheEnd() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }
        final NLUser user = users.get(0);
        user.setDefaultTemplateId(NLStringValue.of("2d2y17fh10"));

        final ELAInstantMailRequest request = MailModuleUtil.createInstantMailRequest(user);
        final ELAInstantMailDetails details = ELAInstantMailDetails.of(request);

        final Map<String, String> params = paramBuilder.buildParamsMap(user, details);
        params.put(ELAParam.SMTP_ACCOUNT, passwordEncoder.decrypt(user.getSmtpAccount().getValue()));

        final String encodedEmail = URLEncoder.encode(user.getEmail().getValue(), StandardCharsets.UTF_8);
        final String cancellationToken = user.getCancellationToken().getValue();
        final String unsubscribeText = String.format("Unsubscribe from newsletter: http://localhost:4200/subscription/cancel?token=%s&email=%s", cancellationToken, encodedEmail);
        final String unsubscribeHtml = String.format("<p><a href=\"http://localhost:4200/subscription/cancel?token=%s&email=%s\">Unsubscribe from newsletter</a></p>", cancellationToken, encodedEmail);

        Assertions.assertNotNull(params);
        Assertions.assertTrue(params.get(ELAParam.TEXT).contains(unsubscribeText));
        Assertions.assertTrue(params.get(ELAParam.HTML).contains(unsubscribeHtml));
        Assertions.assertFalse(params.get(ELAParam.TEXT).contains(unsubscribeHtml));
        Assertions.assertFalse(params.get(ELAParam.HTML).contains(unsubscribeText));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowELAParameterBuildExceptionWhenDefaultTemplateIdNullOrEmpty(String templateId) {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }
        final NLUser user = users.get(0);
        user.setDefaultTemplateId(NLStringValue.of(templateId));
        final ELAInstantMailRequest request = MailModuleUtil.createInstantMailRequest(user);
        final ELAInstantMailDetails details = ELAInstantMailDetails.of(request);

        Assertions.assertThrows(ELAParameterBuildException.class, () -> paramBuilder.buildParamsMap(user, details));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotBuildParams(Map<String, String> map) {
        final String params = paramBuilder.buildUrlEncoded(map);
        Assertions.assertEquals("", params);
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
}
