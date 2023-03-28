package pl.newsler.devenv;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Getter
@Setter
@ConfigurationPropertiesScan
@ConfigurationProperties("newsler.devenv.emaillabs.h2")
public class NewslerH2Properties {
    private String appKey;
    private String secretKey;
    private String smtp;
    private String email;
}
