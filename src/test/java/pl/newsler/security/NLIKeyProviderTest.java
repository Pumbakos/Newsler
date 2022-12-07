package pl.newsler.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"java:S5778"})
class NLIKeyProviderTest {
    private final NLKeyProvider keyProvider = new NLKeyProvider();

    @Test
    void shouldGetKey() {
        byte[] bytes = keyProvider.getKey(NLPublicAlias.PE_PASSWORD);
        char[] chars = keyProvider.getCharKey(NLPublicAlias.PE_PASSWORD);
        Assertions.assertNotNull(bytes);
        Assertions.assertNotNull(chars);
        Assertions.assertEquals(String.valueOf(chars), new String(bytes));
    }

    @Test
    void shouldNotGetKey_valueOf() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> keyProvider.getKey(NLPublicAlias.valueOf("OTHER_KEY")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> keyProvider.getCharKey(NLPublicAlias.valueOf("OTHER_KEY")));
    }
}
