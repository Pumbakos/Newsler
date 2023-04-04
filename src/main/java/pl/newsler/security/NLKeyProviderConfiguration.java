package pl.newsler.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.newsler.internal.PropertiesUtil;
import pl.newsler.internal.exception.ConfigurationException;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@ComponentScan
@Configuration(proxyBeanMethods = false)
class NLKeyProviderConfiguration {
    private final String keyStoreType;
    private final String keyStorePath;
    private final String keyStorePassword;
    private final String protectionPasswordPhrase;
    private final String encodeKeySalt;

    NLKeyProviderConfiguration(final Environment env) {
        this.keyStoreType = env.getProperty("newsler.security.keystore.key-store-type");
        this.keyStorePath = env.getProperty("newsler.security.keystore.key-store-path");
        this.keyStorePassword = env.getProperty("newsler.security.keystore.key-store-password");
        this.protectionPasswordPhrase = env.getProperty("newsler.security.keystore.protection-password-phrase");
        this.encodeKeySalt = env.getProperty("newsler.security.keystore.encode-key-salt");

        if (!PropertiesUtil.arePropsSet(keyStoreType, keyStorePath, keyStorePassword, protectionPasswordPhrase, encodeKeySalt)) {
            throw new ConfigurationException("KeyStore properties not set properly");
        }
    }

    @Bean(name = "keyProvider")
    public NLIKeyProvider keyProvider() {
        try {
            return new NLKeyProvider(keyStoreType, keyStorePath, keyStorePassword, protectionPasswordPhrase, encodeKeySalt);
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException e) {
            throw new ConfigurationException("KeyStore properties not set properly");
        }
    }
}
