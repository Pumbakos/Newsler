package pl.newsler.components.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.newsler.commons.model.NLAppKey;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.commons.model.NLUuid;

import java.util.UUID;

import static pl.newsler.testcommons.TestUserUtils.secretOrAppKey;

@SuppressWarnings("java:S5778")// none of `of()` methods listed below throws any Exception
class NLUserTest {
    private final TestUserFactory factory = new TestUserFactory();

    @Test
    void shouldCompareNLUser() {
        final NLUser standardUser = factory.standard();
        final NLUser dottedUser = factory.dotted();
        NLDUser nldStandardUser = NLDUser.of(standardUser);
        NLDUser nldDottedUser = NLDUser.of(dottedUser);

        Assertions.assertEquals(nldStandardUser, NLDUser.of(standardUser));
        Assertions.assertEquals(nldStandardUser.toString(), nldStandardUser.toString());
        Assertions.assertEquals(nldStandardUser.hashCode(), nldStandardUser.hashCode());
        Assertions.assertNotEquals(nldStandardUser, nldDottedUser);
        Assertions.assertNotEquals(nldStandardUser.toString(), nldDottedUser.toString());
        Assertions.assertNotEquals(nldStandardUser.hashCode(), nldDottedUser.hashCode());
        Assertions.assertNotEquals(nldStandardUser, nldDottedUser);
    }

    @Test
    void shouldGetNLUserProperties() {
        final NLUser standardUser = factory.standard();
        standardUser.setId(NLUuid.of(UUID.randomUUID()));
        standardUser.setAppKey(NLAppKey.of(secretOrAppKey()));
        standardUser.setSecretKey(NLSecretKey.of(secretOrAppKey()));
        standardUser.setSmtpAccount(NLSmtpAccount.of("1.test.smp"));

        Assertions.assertNotNull(standardUser);
        Assertions.assertNotNull(standardUser.getId());
        Assertions.assertNotNull(standardUser.getEmail());
        Assertions.assertNotNull(standardUser.getFirstName());
        Assertions.assertNotNull(standardUser.getLastName());
        Assertions.assertNotNull(standardUser.getNLPassword());
        Assertions.assertNotNull(standardUser.getSmtpAccount());
        Assertions.assertNotNull(standardUser.getSecretKey());
        Assertions.assertNotNull(standardUser.getAppKey());
        Assertions.assertNotNull(standardUser.getRole());
        Assertions.assertNotNull(standardUser.getAuthorities());
        Assertions.assertTrue(standardUser.getAuthorities().contains(new SimpleGrantedAuthority(standardUser.getRole().name())));
        Assertions.assertTrue(standardUser.isEnabled());
        Assertions.assertTrue(standardUser.isCredentialsNonExpired());
        Assertions.assertTrue(standardUser.isAccountNonExpired());
        Assertions.assertTrue(standardUser.isAccountNonLocked());
    }

    @Test
    void shouldCompareNLDUser() {
        final NLUser standardUser = factory.standard();
        final NLUser dottedUser = factory.dotted();

        Assertions.assertEquals(standardUser, factory.standard());
        Assertions.assertEquals(standardUser.toString(), standardUser.toString());
        Assertions.assertEquals(standardUser.hashCode(), standardUser.hashCode());
        Assertions.assertNotEquals(standardUser, NLDUser.of(dottedUser));
        Assertions.assertNotEquals(standardUser, null);
        Assertions.assertNotEquals(standardUser, dottedUser);
        Assertions.assertNotEquals(standardUser.toString(), dottedUser.toString());
        Assertions.assertNotEquals(standardUser.hashCode(), dottedUser.hashCode());
        Assertions.assertNotEquals(standardUser, dottedUser);
    }

    @Test
    void shouldGetNLDUserProperties() {
        final NLUser standardUser = factory.standard();

        NLDUser nldUser = NLDUser.of(standardUser);
        Assertions.assertNotNull(nldUser);
        Assertions.assertNotNull(nldUser.toString());
        Assertions.assertEquals(standardUser.getId(), nldUser.getId());
        Assertions.assertEquals(standardUser.getEmail(), nldUser.getEmail());
        Assertions.assertEquals(standardUser.getFirstName(), nldUser.getName());
        Assertions.assertEquals(standardUser.getLastName(), nldUser.getLastName());
        Assertions.assertEquals(standardUser.getNLPassword(), nldUser.getPassword());
        Assertions.assertEquals(standardUser.getSmtpAccount(), nldUser.getSmtpAccount());
        Assertions.assertEquals(standardUser.getSecretKey(), nldUser.getSecretKey());
        Assertions.assertEquals(standardUser.getAppKey(), nldUser.getAppKey());
        Assertions.assertEquals(standardUser.getRole(), nldUser.getRole());
        Assertions.assertEquals(standardUser.isEnabled(), nldUser.isEnabled());
        Assertions.assertEquals(standardUser.isCredentialsNonExpired(), nldUser.isCredentialsNonExpired());
        Assertions.assertEquals(nldUser.toString(), NLDUser.of(standardUser).toString());
    }
}
