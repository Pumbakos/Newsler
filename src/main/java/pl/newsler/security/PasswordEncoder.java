package pl.newsler.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.newsler.api.auth.AlgorithmType;
import pl.newsler.api.auth.JWTClaim;
import pl.newsler.exceptions.implemenation.DecryptionException;
import pl.newsler.exceptions.implemenation.EncryptionException;
import pl.newsler.exceptions.implemenation.KeyInitializationException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;


@Configuration
public class PasswordEncoder {
    private static final byte[] SALT = new byte[]{-94, 119, -103, -55, -18, 5, -64, 60, -97, -92, 72, 10, 98, -3, 54};
    private static final SecretKey secretKey = generateKey();

    public static byte[] salt() {
        return new String(JWTClaim.JWT_ID).getBytes(StandardCharsets.UTF_8);
    }

    @Bean
    public BCryptPasswordEncoder bCrypt() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 8);
    }

    public final String encrypt(String string, AlgorithmType algorithm) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm.getName());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(string.getBytes());
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            throw new EncryptionException(e.getMessage(), "");
        }
    }


    public final String decrypt(String string, AlgorithmType algorithm) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm.getName());
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(string));
            return new String(plainText);
        } catch (Exception e) {
            throw new DecryptionException(e.getMessage(), "");
        }
    }

    private static SecretKey generateKey() {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(JWTClaim.JWT_ID, SALT, 65536, 256);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new KeyInitializationException(e.getMessage(), e.getCause().toString());
        }
    }
}

