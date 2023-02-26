package pl.newsler.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.newsler.commons.exception.EncryptionException;

class NLIPasswordEncoderTest {
    private final NLPasswordEncoderConfiguration configuration = new NLPasswordEncoderConfiguration();
    private final NLPasswordEncoder passwordEncoder = configuration.passwordEncoder(configuration.bCryptPasswordEncoder());

    @Test
    void shouldEncryptAndDecryptData() {
        String plainText = "2a83b005-25f3-471d-b5a4-78d547b51fcd";
        String encrypted = passwordEncoder.encrypt(plainText);
        Assertions.assertNotNull(encrypted);

        String decrypted = passwordEncoder.decrypt(encrypted);
        Assertions.assertNotNull(decrypted);
        Assertions.assertEquals(plainText, decrypted);

        String emptyText = "";
        String encryptedEmpty = passwordEncoder.encrypt(emptyText);
        Assertions.assertNotNull(encryptedEmpty);

        String decryptedEmpty = passwordEncoder.decrypt(encryptedEmpty);
        Assertions.assertNotNull(decryptedEmpty);
        Assertions.assertEquals(emptyText, decryptedEmpty);
    }

    @Test
    void shouldNotEncryptWhenStringIsNullAndThrowEncryptionException() {
        Assertions.assertThrows(EncryptionException.class, () -> passwordEncoder.encrypt(null));
    }

    @Test
    void shouldMatchBCryptedPassword() {
        String rawPassword = "qeWp0YF9MosU";
        String encoded = passwordEncoder.bCrypt().encode(rawPassword);
        Assertions.assertTrue(passwordEncoder.bCrypt().matches(rawPassword, encoded));
    }
}
