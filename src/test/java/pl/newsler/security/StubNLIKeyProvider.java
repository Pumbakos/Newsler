package pl.newsler.security;

import org.springframework.core.env.Environment;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class StubNLIKeyProvider implements NLIKeyProvider {
    private final NLKeyProviderConfiguration configuration;

    StubNLIKeyProvider(final Environment env) {
        configuration = new NLKeyProviderConfiguration(env);
    }

    @Override
    public byte[] getKey(String alias) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        return configuration.keyProvider().getKey(alias);
    }

    @Override
    public char[] getCharKey(String alias) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        return configuration.keyProvider().getCharKey(alias);
    }
}
