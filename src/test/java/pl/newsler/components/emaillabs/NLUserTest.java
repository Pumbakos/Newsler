package pl.newsler.components.emaillabs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.newsler.commons.model.NLAppKey;
import pl.newsler.commons.model.NLIdType;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.executor.ELAInstantMailDetails;
import pl.newsler.components.emaillabs.usecase.ELAInstantMailRequest;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.security.StubNLPasswordEncoder;
import pl.newsler.testcommons.TestUserUtils;

import java.util.List;
import java.util.UUID;

import static pl.newsler.components.emaillabs.MailModuleUtil.createInstantMailRequest;

public class NLUserTest {
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubUserRepository userRepository = new StubUserRepository();
    private final TestUserFactory factory = new TestUserFactory();

    @BeforeEach
    void beforeEach() {
        NLUuid standardId = NLUuid.of(UUID.randomUUID());
        factory.standard().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.standard_plainPassword())));
        factory.standard().setUuid(standardId);
        factory.standard().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.standard().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.standard().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        userRepository.save(factory.standard());

        NLUuid dashedId = NLUuid.of(UUID.randomUUID());
        factory.dashed().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dashed_plainPassword())));
        factory.dashed().setUuid(dashedId);
        factory.dashed().setAppKey(NLAppKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dashed().setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(TestUserUtils.secretOrAppKey())));
        factory.dashed().setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(TestUserUtils.smtpAccount())));
        userRepository.save(factory.dashed());

        NLUuid dottedId = NLUuid.of(UUID.randomUUID());
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
    @SuppressWarnings({"java:S5863"})
    void shouldCompareNLUserMail() {
        final List<NLUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            Assertions.fail("Users empty");
        }
        final NLUser user = users.get(0);
        final ELAInstantMailRequest requestForFirst = createInstantMailRequest(user);
        final ELAInstantMailRequest requestForSecond = new ELAInstantMailRequest(
                user.getEmail().getValue(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
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
        final ELAInstantMailRequest request = createInstantMailRequest(user);
        final ELAUserMail first = ELAUserMail.of(NLUuid.of(UUID.randomUUID(), NLIdType.MAIL), ELAInstantMailDetails.of(request));

        Assertions.assertNotNull(first);
        Assertions.assertNotNull(first.getUuid());
        Assertions.assertNotNull(first.getUserId());
        Assertions.assertNotNull(first.getMessage());
        Assertions.assertNotNull(first.getStatus());
        Assertions.assertNotNull(first.getToAddresses());
        Assertions.assertNotNull(first.getVersion());
        Assertions.assertTrue(first.getErrorMessage().getValue().isEmpty());
    }

}
