package pl.newsler.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@ComponentScan
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
class NLKeyProviderConfiguration {
    @Value("${newsler.security.keystore.key-store-type}")
    private String keyStoreType;
    @Value("${newsler.security.keystore.key-store-path}")
    private String keyStorePath;
    @Value("${newsler.security.keystore.key-store-password}")
    private String keyStorePassword;
    @Value("${newsler.security.keystore.protection-password-phrase}")
    private String protectionPasswordPhrase;
    @Value("${newsler.security.keystore.encode-key-salt}")
    private String encodeKeySalt;

    @Bean(name = "keyProvider")
    public NLIKeyProvider keyProvider() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        if (!StringUtils.isAllBlank(keyStoreType, keyStorePath, keyStorePassword, protectionPasswordPhrase, encodeKeySalt)) {
            return new NLKeyProvider(keyStoreType, keyStorePath, keyStorePassword, protectionPasswordPhrase, encodeKeySalt);
        }

        throw new IllegalArgumentException("KeyStore properties not fully set");
    }
}
