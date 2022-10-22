package pl.newsler.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pl.newsler.auth.JWTClaim;
import pl.newsler.security.exception.AlgorithmInitializatoinException;
import pl.newsler.security.exception.DecryptionException;
import pl.newsler.security.exception.EncryptionException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class NLPasswordEncoder {
    private static final byte[] SALT = NLKeyStore.getKey(NLAlias.PE_SALT);
    private static final SecretKey secretKey = generateKey();

    public static byte[] salt() {
        return String.valueOf(JWTClaim.JWT_ID).getBytes(StandardCharsets.UTF_8);
    }

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
            SecretKeyFactory factory = SecretKeyFactory.getInstance(AlgorithmType.PBE_WITH_HMAC_SHA256_AND_AES256.getName());
            KeySpec spec = new PBEKeySpec(JWTClaim.JWT_ID, SALT, 65536, 256);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AlgorithmInitializatoinException(e.getMessage(), e.getCause().toString());
        }
    }
}

