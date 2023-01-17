package pl.newsler.internal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@ConfigurationProperties("newsler")
@Getter
@Setter
public class DomainProperties {
    private Schema schema = Schema.HTTP;
    private String domainName;
    private String ip;
    private int port = 8080;

    public enum Schema {
        HTTPS, HTTP
    }
}