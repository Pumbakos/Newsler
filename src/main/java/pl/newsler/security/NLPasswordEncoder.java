package pl.newsler.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.newsler.commons.exception.DecryptionException;
import pl.newsler.commons.exception.EncryptionException;
import pl.newsler.security.exception.AlgorithmInitializationException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

class NLPasswordEncoder implements NLIPasswordEncoder {
    private final char[] password = new char[]{'w', 'l', '8', 'k', 'e', 'J', 'k', 't', 'x', 'R', 'x', 'Q', 'I', 'S', 'u', 'Y', '5', 'j', '4', 'g', 'M', '9', 'W', 'X', '6', 'V', 'l', 'V', 'Q', '4', '9', 'M'};
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecretKey secretKey;
    private final byte[] salt;

    NLPasswordEncoder(final BCryptPasswordEncoder bCryptPasswordEncoder, final NLIKeyProvider keyProvider, final Environment env) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        final String property = env.getProperty("newsler.security.keystore.encode-key-salt");
        this.salt = property != null ? property.getBytes() : keyProvider.getKey("newsler.security.keystore.encode-key-salt");
        this.secretKey = generateKey();
    }

    @Override
    public BCryptPasswordEncoder bCrypt() {
        return bCryptPasswordEncoder;
    }

    @Override
    public final @NotNull String encrypt(@NotNull String string) {
        try {
            Cipher cipher = Cipher.getInstance(AlgorithmType.AES.toString());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(string.getBytes());
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            throw new EncryptionException();
        }
    }

    @Override
    public final @NotNull String decrypt(@NotNull String string) {
        try {
            Cipher cipher = Cipher.getInstance(AlgorithmType.AES.toString());
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(string));
            return new String(plainText);
        } catch (Exception e) {
            throw new DecryptionException();
        }
    }

    private SecretKey generateKey() {
        try {
            // FIXME: generate key depending on user's salt
            SecretKeyFactory factory = SecretKeyFactory.getInstance(AlgorithmType.PBE_WITH_HMAC_SHA256_AND_AES256.toString());
            KeySpec spec = new PBEKeySpec(password, salt, Short.MAX_VALUE, 256); //iterations?
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), AlgorithmType.AES.toString());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AlgorithmInitializationException(e.getMessage(), e.getCause().toString());
        }
    }
}

