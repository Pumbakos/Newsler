package pl.newsler.internal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Getter
@Setter
@ConfigurationPropertiesScan
@ConfigurationProperties("newsler.security.jwt")
public class NewslerJwtProperties {
    private String keyStorePath;
    private String keyStorePassword;
    private String keyAlias;
}
