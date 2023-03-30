package pl.newsler.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.newsler.testcommons.environment.KeyStorePropsStrategy;
import pl.newsler.testcommons.environment.StubEnvironment;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@SuppressWarnings({"java:S5778"})
class NLIKeyProviderTest {
    private final NLIKeyProvider keyProvider = new StubNLIKeyProvider(new StubEnvironment(new KeyStorePropsStrategy()));

    @Test
    void shouldGetKey() {
        try {
            byte[] bytes = keyProvider.getKey("newsler.security.keystore.encode-key-salt");
            char[] chars = keyProvider.getCharKey("newsler.security.keystore.encode-key-salt");
            Assertions.assertNotNull(bytes);
            Assertions.assertNotNull(chars);
            Assertions.assertEquals(String.valueOf(chars), new String(bytes));
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException e) {
            Assertions.fail();
        }
    }

    @Test
    void shouldNotGetKey_valueOf() {
        Assertions.assertThrows(NullPointerException.class, () -> keyProvider.getKey("OTHER_KEY"));
        Assertions.assertThrows(NullPointerException.class, () -> keyProvider.getCharKey("OTHER_KEY"));
    }
}
