package pl.newsler.security;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.newsler.security.exception.AlgorithmInitializationException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@RequiredArgsConstructor
class NLPasswordEncoder implements NLIPasswordEncoder {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final byte[] salt = NLKeyStore.getKey(NLAlias.PE_SALT);
    private final char[] password = new char[]{'w', 'l', '8', 'k', 'e', 'J', 'k', 't', 'x', 'R', 'x', 'Q', 'I', 'S', 'u', 'Y', '5', 'j', '4', 'g', 'M', '9', 'W', 'X', '6', 'V', 'l', 'V', 'Q', '4', '9', 'M'};
    private final SecretKey secretKey = generateKey();

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
            throw new IllegalArgumentException();
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
            throw new IllegalArgumentException();
        }
    }

    private SecretKey generateKey() {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(AlgorithmType.PBE_WITH_HMAC_SHA256_AND_AES256.toString());
            KeySpec spec = new PBEKeySpec(password, salt, Short.MAX_VALUE, 256); //iterations?
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), AlgorithmType.AES.toString());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AlgorithmInitializationException(e.getMessage(), e.getCause().toString());
        }
    }
}

