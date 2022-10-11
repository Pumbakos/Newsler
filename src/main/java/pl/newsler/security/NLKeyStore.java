package pl.newsler.security;

import org.apache.tomcat.util.codec.binary.Base64;
import pl.newsler.auth.JWTClaim;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class NLKeyStore {
//    private static final char[] PWD = new char[]{94, 119, 103, 55, 18, 5, 64, 60, 97, 92, 72, 10, 98, 3, 54, 84, 65, 121, 47, 124, 121, 79, 34, 14};
    private static final byte[] PWD = "9acb433f-37e2-479c-91b9-3f28039ec7ca".getBytes(StandardCharsets.UTF_8);
    private static final char[] PWD2 = "9acb433f-37e2-479c-91b9-3f28039ec7ca".toCharArray();
    private static final byte[] SALT = new byte[]{-94, 119, -103, -55, -18, 5, -64, 60, -97, -92, 72, 10, 98, -3, 54};
    private static final SecureRandom random = new SecureRandom();
    // FIXME: use environmental variables in docker
    private static final String NEWSLER_KEYSTORE_PASSWORD = System.getenv("NEWSLER_KEYSTORE_PASSWORD");
    private static final String NEWSLER_APP_KEY = System.getenv("NEWSLER_APP_KEY");
    private static final String NEWSLER_SECRET_KEY = System.getenv("NEWSLER_SECRET_KEY");
    private static final String NEWSLER_SMTP = System.getenv("NEWSLER_SMTP");

    public static void main(String[] args) throws Exception {
        File file = new File("D:\\Desktop\\Newsler\\Newsler\\src\\main\\resources\\keystore\\keystore.p12");
        InputStream stream = new FileInputStream(file);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(stream, NEWSLER_KEYSTORE_PASSWORD.toCharArray());
//        PrivateKey key = (PrivateKey) keyStore.getKey("example", );
//        // generate RSA key pair
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(AlgorithmType.RSA.getName());
//        keyPairGenerator.initialize(2048);
//        KeyPair keyPair = keyPairGenerator.genKeyPair();
//        keyStore.setKeyEntry("NEWSLER_APP_KEY", encryptPrivateKey(NEWSLER_APP_KEY, keyPair, 100_000), getCertificate());
        final TriDES triDES = new TriDES(PWD);
        final byte[] encrypt = triDES.encrypt(NEWSLER_APP_KEY.getBytes(StandardCharsets.UTF_8));
        final byte[] decrypt = triDES.decrypt(encrypt);

        final SecretKey secretKey = generateKey(new String(encrypt).toCharArray());
        final KeyStore.PasswordProtection protParam = new KeyStore.PasswordProtection(PWD2);
        keyStore.setEntry("NEWSLER_APP_KEY", new KeyStore.SecretKeyEntry(secretKey), protParam);
//        final EncryptedPrivateKeyInfo keyInfo = new EncryptedPrivateKeyInfo(generateKey(NEWSLER_APP_KEY.toCharArray()).getEncoded());
//        keyStore.setKeyEntry(
//                "NEWSLER_APP_KEY",
//                keyInfo.,
//                null
//        );
        KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry("NEWSLER_APP_KEY", protParam);
        final SecretKey secretKey1 = entry.getSecretKey();
//        final KeyStore.SecretKeyEntry appKey = (KeyStore.SecretKeyEntry) keyStore.getEntry("NEWSLER_APP_KEY", protParam);
//        final byte[] encoded = appKey.getSecretKey().getEncoded();
//        final SecretKey decodeKey = decodeKey(appKey);
//        final byte[] encoded = decodeKey.getEncoded();
//        final String format = decodeKey.getFormat();
//
////        byte[] encryptedPkcs8 = encryptPrivateKey(NEWSLER_APP_KEY, keyPair, 100_000);
//        byte[] encryptedPkcs8 = encodeTest(NEWSLER_APP_KEY);
//
//        ks.setKeyEntry("NEWSLER_APP_KEY", encryptedPkcs8, getCertificate());
//        FileOutputStream fos = new FileOutputStream("privkey.p8");
//        fos.write(encryptedPkcs8);
//        fos.close();
    }

//    private static Certificate[] getCertificate() throws NoSuchAlgorithmException, KeyStoreException {
//
//        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        List<Certificate> x509Certificates = new ArrayList<>();
//        trustManagerFactory.init((KeyStore) null);
//        Arrays.stream(trustManagerFactory.getTrustManagers()).forEach(t -> x509Certificates.addAll(Arrays.asList(((X509TrustManager) t).getAcceptedIssuers())));
//
//        return x509Certificates.toArray(Certificate[]::new);
//    }

//    private static byte[] encryptPrivateKey(String password, KeyPair keyPair, int count) throws Exception {
//        // extract the encoded private key, this is an unencrypted PKCS#8 private key
//        byte[] encodedPrivateKey = keyPair.getPrivate().getEncoded();
//
//        // Use a PasswordBasedEncryption (PBE) algorithm, OID of this algorithm will be saved
//        // in the PKCS#8 file, so changing it (when more standard algorithm or safer
//        // algorithm is available) doesn't break backwards compatibility.
//        // In other words, decryptor doesn't need to know the algorithm before it will be
//        // able to decrypt the PKCS#8 object.
//        String algorithm = AlgorithmType.PBE_WITH_HMAC_SHA256_AND_AES256.getName();
//
//        // Create PBE parameter set
//        PBEParameterSpec parameterSpec = new PBEParameterSpec(SALT, count);
//        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
//        SecretKey secretKey = keyFactory.generateSecret(keySpec);
//
//        Cipher cipherInstance = Cipher.getInstance(algorithm);
//
//        // Initialize PBE Cipher with key and parameters
//        cipherInstance.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
//
//        // Encrypt the encoded Private Key with the PBE key
//        byte[] ciphertext = cipherInstance.doFinal(encodedPrivateKey);
//
//        // Now construct  PKCS #8 EncryptedPrivateKeyInfo object
////        AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(AlgorithmType.AES.getName());
////        final RSAKeyGenParameterSpec rsaKeyGenParameterSpec = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4);
////        algorithmParameters.init(rsaKeyGenParameterSpec);
//        EncryptedPrivateKeyInfo privateKeyInfo = new EncryptedPrivateKeyInfo(AlgorithmType.AES.getName(), ciphertext);
//
//        // DER encoded PKCS#8 encrypted key
//        return privateKeyInfo.getEncoded();
//    }

//    private static byte[] encodeTest(String password) throws Exception {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(AlgorithmType.RSA.getName());
//        keyPairGenerator.initialize(2048);
//
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        PrivateKey privateKey = keyPair.getPrivate();
//
//        Key aesKey = new SecretKeySpec(new EncryptedPrivateKeyInfo(generateKey(NEWSLER_APP_KEY.getBytes(StandardCharsets.UTF_8)).getEncoded()).getEncoded(), AlgorithmType.AES.getName());
//        Cipher cipher = Cipher.getInstance(AlgorithmType.AES.getName());
//        // encrypt the text
//        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
//        return cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
//    }

    private static SecretKey generateKey(char[] password) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(AlgorithmType.PBE_WITH_HMAC_SHA256_AND_AES256.getName());
        KeySpec spec = new PBEKeySpec(password, SALT, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), AlgorithmType.AES.getName());
    }

    private static SecretKey decodeKey(KeyStore.SecretKeyEntry entry) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(AlgorithmType.PBE_WITH_HMAC_SHA256_AND_AES256.getName());
        return factory.translateKey(entry.getSecretKey());
    }

    private static class TriDES {
        private final Cipher cipher;
        private final SecretKey key;

        public TriDES(byte[] encKey) throws Exception {
            KeySpec keySpec = new DESedeKeySpec(encKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(AlgorithmType.TRIPLE_DES.getName());
            cipher = Cipher.getInstance(AlgorithmType.TRIPLE_DES.getName());
            key = keyFactory.generateSecret(keySpec);
        }


        public byte[] encrypt(byte[] bytes) throws Exception {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeBase64(cipher.doFinal(bytes));
        }


        public byte[] decrypt(byte[] bytes) throws Exception {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(Base64.decodeBase64(bytes));
        }
    }
}