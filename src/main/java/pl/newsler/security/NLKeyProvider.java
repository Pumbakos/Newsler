package pl.newsler.security;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

class NLKeyProvider implements NLIKeyProvider {
    private final NLKeyStore keyStore;

    NLKeyProvider(String keyStoreType, String keyStorePath, String keyStorePassword,
                  String protectionPasswordPhrase, String encodeKeySalt) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        this.keyStore = new NLKeyStore(keyStoreType, keyStorePath, keyStorePassword, protectionPasswordPhrase, encodeKeySalt);
    }

    @Override
    public byte[] getKey(String alias) {
        return keyStore.getKey(alias);
    }

    @Override
    @SuppressWarnings("java:S2129")
    public char[] getCharKey(String alias) {
        return new String(keyStore.getKey(alias)).toCharArray();
    }
}
