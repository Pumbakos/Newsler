package pl.palubiak.dawid.newsler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationPropertiesScan()
@EnableConfigurationProperties
@SpringBootApplication
public class NewslerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewslerApplication.class, args);
    }
}
