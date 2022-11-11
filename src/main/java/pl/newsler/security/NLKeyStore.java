package pl.newsler.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import pl.newsler.exceptions.NoResourceFoundException;
import pl.newsler.exceptions.RegexNotMatchException;
import pl.newsler.security.exception.AlgorithmInitializatoinException;
import pl.newsler.security.exception.DecryptionException;
import pl.newsler.security.exception.EncryptionException;
import pl.newsler.security.exception.KetStoreInitializationException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class NLKeyStore {
    private static final TriDES triDES;
    private static final KeyStore keyStore;
    private static final String KEYSTORE_PASSWORD;
    private static final byte[] SALT;
    private static final byte[] PWD = new byte[]{
            80, 121, 87, 68, 83, 54, 110, 98, 83, 51, 88, 48, 122, 116, 117, 71, 49, 89, 90, 57, 68, 104, 84, 73, 66,
            112, 48, 72, 110, 109, 117, 103, 115, 109, 102, 99, 112, 80, 84, 54, 88, 82, 78, 111, 65, 100, 82, 80, 70,
            76, 70, 99, 50, 53, 56, 78, 107, 76, 78, 73, 101, 51, 104, 70, 87, 79, 90, 85, 85, 72, 73, 86, 87, 70, 111,
            69, 117, 88, 52, 65, 70, 99, 78, 69, 108, 79, 118, 89, 81, 66, 105, 83, 70, 88, 112, 105, 114, 115, 75, 51,
            117, 68, 121, 114, 78, 52, 122, 105, 71, 108, 75, 120, 83, 84, 105, 113, 89, 76, 54, 106, 81, 67, 54, 104,
            117, 69, 97, 111
    };

    static {
        try {
            triDES = new TriDES(PWD);

            final Optional<InputStream> saltResource = ResourceLoaderFactory.getSaltResource();
            if (saltResource.isEmpty()) {
                throw new FileNotFoundException("Could not find file containing salt");
            }
            BufferedInputStream saltBIS = new BufferedInputStream(saltResource.get());
            SALT = triDES.decrypt(saltBIS.readAllBytes());

            final Optional<InputStream> kpResource = ResourceLoaderFactory.getKeystorePasswordResource();
            if (kpResource.isEmpty()) {
                throw new FileNotFoundException("Could not find file containing salt");
            }
            BufferedInputStream keystorePasswordBIS = new BufferedInputStream(kpResource.get());
            KEYSTORE_PASSWORD = new String(triDES.decrypt(keystorePasswordBIS.readAllBytes()));

            if (!KEYSTORE_PASSWORD.matches("^[a-zA-Z0-9\"\\\\{},.><~|/\\[]*$")) {
                throw new RegexNotMatchException("KeyStore password", "Password does not match regex");
            }

            final Optional<InputStream> optionalStream = ResourceLoaderFactory.getKeystoreResource();
            if (optionalStream.isEmpty()) {
                throw new NoResourceFoundException("KeyStore resource not found", "Could not find keystore file");
            }

            final InputStream stream = optionalStream.get();
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(stream, KEYSTORE_PASSWORD.toCharArray());
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new KetStoreInitializationException("Error while reading keystore file.", e.getMessage());
        }
    }

    static byte[] getKey(NLAlias alias) {
        try {
            final KeyStore.PasswordProtection aliasPasswordProtection = new KeyStore.PasswordProtection(new String(PWD).toCharArray());
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias.getName(), aliasPasswordProtection);
            return triDES.decrypt(secretKeyEntry.getSecretKey().getEncoded());
        } catch (UnrecoverableEntryException | KeyStoreException | NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
            return new byte[]{};
        }
    }

    static byte[] getKey(NLPublicAlias alias) {
        try {
            final KeyStore.PasswordProtection aliasPasswordProtection = new KeyStore.PasswordProtection(new String(PWD).toCharArray());
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias.getName(), aliasPasswordProtection);
            return triDES.decrypt(secretKeyEntry.getSecretKey().getEncoded());
        } catch (UnrecoverableEntryException | KeyStoreException | NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
            return new byte[]{};
        }
    }

    static void setKey(String alias, String key) {
        try {
            final Optional<File> keystoreResource = ResourceLoaderFactory.getKeystoreResourceAsFile();
            if (keystoreResource.isEmpty()) {
                throw new FileNotFoundException("Could not load keystore file");
            }
            Optional<File> optionalFile = ResourceLoaderFactory.getKeystoreResourceAsFile();
            if (optionalFile.isEmpty()) {
                throw new SecurityException();
            }

            final File file = optionalFile.get();
            final byte[] encryptedKey = triDES.encrypt(key.getBytes(StandardCharsets.UTF_8));
            final SecretKey secretKey = encodeKey(new String(encryptedKey).toCharArray());
            final KeyStore.PasswordProtection keyParam = new KeyStore.PasswordProtection(new String(PWD).toCharArray());
            keyStore.setEntry(alias, new KeyStore.SecretKeyEntry(secretKey), keyParam);
            keyStore.store(new BufferedOutputStream(new FileOutputStream(file)), KEYSTORE_PASSWORD.toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new EncryptionException("Could not set new key", e.getMessage());
        }
    }

    private static SecretKey encodeKey(char[] password) throws EncryptionException {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(AlgorithmType.PBE_WITH_HMAC_SHA256_AND_AES256.toString());
            KeySpec spec = new PBEKeySpec(password, SALT, 65536, 256);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), AlgorithmType.AES.toString());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncryptionException("Error while encoding secret key", e.getMessage());
        }
    }

    private static SecretKey decodeKey(KeyStore.SecretKeyEntry entry) throws DecryptionException {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(AlgorithmType.PBE_WITH_HMAC_SHA256_AND_AES256.toString());
            return factory.translateKey(entry.getSecretKey());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new DecryptionException("Error while decoding secret key", e.getMessage());
        }
    }

    private static class TriDES {
        final Cipher cipher;
        final SecretKey key;

        TriDES(byte[] encKey) throws AlgorithmInitializatoinException {
            try {
                KeySpec keySpec = new DESedeKeySpec(encKey);
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(AlgorithmType.TRIPLE_DES.toString());
                cipher = Cipher.getInstance(AlgorithmType.TRIPLE_DES.toString());
                key = keyFactory.generateSecret(keySpec);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException e) {
                throw new AlgorithmInitializatoinException(e.getMessage(), e.getCause().toString());
            }
        }

        byte[] encrypt(byte[] bytes) throws EncryptionException {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return Base64.encodeBase64(cipher.doFinal(bytes));
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                throw new EncryptionException("Error while encrypting key", e.getMessage());
            }
        }

        byte[] decrypt(byte[] bytes) throws DecryptionException {
            try {
                cipher.init(Cipher.DECRYPT_MODE, key);
                return cipher.doFinal(Base64.decodeBase64(bytes));
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                throw new DecryptionException("Error while decrypting key", e.getMessage());
            }
        }
    }
}