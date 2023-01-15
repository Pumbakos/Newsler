package pl.newsler;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import pl.newsler.internal.SpringBootStartUpPropsResolver;

import java.util.Properties;

@SpringBootApplication
public class NewslerApplication {
    public static void main(String[] args) {
        Properties props = SpringBootStartUpPropsResolver.resolve(args);

        new SpringApplicationBuilder(NewslerApplication.class)
                .properties(props)
                .run(args);
    }
}
