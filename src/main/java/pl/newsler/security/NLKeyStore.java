package pl.newsler.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import pl.newsler.security.exception.AlgorithmInitializationException;
import pl.newsler.commons.exception.DecryptionException;
import pl.newsler.commons.exception.EncryptionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
final class NLKeyStore {
    private final TriDES triDES;
    private final KeyStore keyStore;
    private final String keystorePassword;
    private final byte[] salt;
    private final byte[] pwd;

    public NLKeyStore(String keyStoreType, String keyStorePath, String keyStorePassword,
                      String protectionPasswordPhrase, String encodeKeySalt) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        this.salt = encodeKeySalt.getBytes();
        this.pwd = protectionPasswordPhrase.getBytes();
        this.keystorePassword = keyStorePassword;
        triDES = new TriDES(protectionPasswordPhrase.getBytes());

        this.keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
    }

    byte[] getKey(String alias) {
        try {
            final KeyStore.PasswordProtection aliasPasswordProtection = new KeyStore.PasswordProtection(new String(pwd).toCharArray());
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, aliasPasswordProtection);
            return triDES.decrypt(secretKeyEntry.getSecretKey().getEncoded());
        } catch (UnrecoverableEntryException | KeyStoreException | NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
            return new byte[]{};
        }
    }

//    public static void main(String[] args) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
//        NLKeyStore store = new NLKeyStore(
//                "PKCS12",
//                "D:\\Desktop\\Newsler\\Newsler\\src\\main\\resources\\keystore\\keystore.p12",
//                "7>Oz\"}bdQM<,~J|[{wT>Ww/5<",
//                "PyWDS6nbS3X0ztuG1YZ9DhTIBp0HnmugsmfcpPT6XRNoAdRPFLFc258NkLNIe3hFWOZUUHIVWFoEuX4AFcNElOvYQBiSFXpirsK3uDyrN4ziGlKxSTiqYL6jQC6huEao",
//                "yPjTXiROwkvhuhxyddSIiYVlBsWTi4mnj6zUPcCgCK4RkJMBc88ZcQGOj5byoTGhygTFtiA25NB8INVCts1V3gOd0mLrA2qRMNK1LytlYPj3croHPTgHytrfV0X8auUj"
//        );
//
//        byte[] appKey = store.getKey("newsler_app_key");
//    }

    void setKey(String alias, String key) {
        try {
            final Optional<File> optionalFile = ResourceLoaderFactory.getKeystoreResourceAsFile();
            if (optionalFile.isEmpty()) {
                throw new SecurityException();
            }

            final File file = optionalFile.get();
            final byte[] encryptedKey = triDES.encrypt(key.getBytes(StandardCharsets.UTF_8));
            final SecretKey secretKey = encodeKey(new String(encryptedKey).toCharArray());
            final KeyStore.PasswordProtection keyParam = new KeyStore.PasswordProtection(new String(pwd).toCharArray());
            keyStore.setEntry(alias, new KeyStore.SecretKeyEntry(secretKey), keyParam);
            keyStore.store(new BufferedOutputStream(new FileOutputStream(file)), keystorePassword.toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new EncryptionException("Could not set new key", e.getMessage());
        }
    }

    private SecretKey encodeKey(char[] password) throws EncryptionException {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(AlgorithmType.PBE_WITH_HMAC_SHA256_AND_AES256.toString());
            KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), AlgorithmType.AES.toString());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncryptionException("Error while encoding secret key", e.getMessage());
        }
    }

    private SecretKey decodeKey(KeyStore.SecretKeyEntry entry) throws DecryptionException {
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

        TriDES(byte[] encKey) throws AlgorithmInitializationException {
            try {
                KeySpec keySpec = new DESedeKeySpec(encKey);
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(AlgorithmType.TRIPLE_DES.toString());
                cipher = Cipher.getInstance(AlgorithmType.TRIPLE_DES.toString());
                key = keyFactory.generateSecret(keySpec);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException |
                     InvalidKeySpecException e) {
                throw new AlgorithmInitializationException(e.getMessage(), e.getCause().toString());
            }
        }

        byte[] encrypt(byte[] bytes) throws EncryptionException {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return Base64.encodeBase64(cipher.doFinal(bytes), false);
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                throw new EncryptionException("Error while encrypting key", e.getMessage());
            }
        }

        byte[] decrypt(byte[] bytes) throws DecryptionException {
            try {
                cipher.init(Cipher.DECRYPT_MODE, key);
                return cipher.doFinal(Base64.decodeBase64(bytes, 0, bytes.length));
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                throw new DecryptionException("Error while decrypting key", e.getMessage());
            }
        }
    }
}