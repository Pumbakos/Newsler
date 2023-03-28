package pl.newsler;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.newsler.devenv.NewslerH2Properties;
import pl.newsler.internal.NewslerDesignerServiceProperties;
import pl.newsler.internal.NewslerJwtProperties;
import pl.newsler.internal.NewslerServiceProperties;
import pl.newsler.internal.SpringBootStartUpPropsResolver;

import java.util.Properties;

@EnableConfigurationProperties(value = {
        NewslerServiceProperties.class, NewslerDesignerServiceProperties.class,
        NewslerJwtProperties.class, NewslerH2Properties.class
})
@SpringBootApplication
public class NewslerApplication {
    public static void main(String[] args) {
        Properties props = SpringBootStartUpPropsResolver.resolve(args);

        new SpringApplicationBuilder(NewslerApplication.class)
                .properties(props)
                .run(args);
    }
}
