package pl.newsler.internal;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Getter
@Setter
@ConfigurationPropertiesScan
@ConfigurationProperties("newsler.designer")
public class NewslerDesignerServiceProperties {
    private NewslerServiceProperties.Schema schema = NewslerServiceProperties.Schema.HTTP;
    private String domainName = "localhost";
    private String ip;
    private int port = 4200;

    public enum Schema {
        HTTPS("https"),
        HTTP("http");
        private final String name;

        Schema(final String name) {
            this.name = name;
        }

        public @NotNull String getName() {
            return name;
        }

        @Override
        public @NotNull String toString() {
            return name;
        }
    }
}
