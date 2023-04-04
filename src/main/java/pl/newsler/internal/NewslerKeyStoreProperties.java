package pl.newsler.internal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Getter
@Setter
@ConfigurationPropertiesScan
@ConfigurationProperties("newsler.security.keystore")
public class NewslerKeyStoreProperties {
    private String keyStoreType;
    private String keyStorePath;
    private String keyStorePassword;
    private String keyAlias;
    /**
     * Protection Password to obtain secret key entry.
     */
    private String protectionPasswordPhrase;
    /**
     * Used to encode new secret key entry.
     */
    private String encodeKeySalt;

    private String appKeyAlias;
    private String secretKeyAlias;
    private String smtpAlias;
    private String emailAlias;
}
