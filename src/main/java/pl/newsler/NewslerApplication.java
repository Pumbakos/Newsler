package pl.newsler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Arrays;
import java.util.Properties;

@SpringBootApplication
public class NewslerApplication {
    public static void main(String[] args) {
        Properties props = new Properties();

        // TODO: CLIArgsResolver
        String ks = Arrays.stream(args).filter(arg -> arg.contains("newsler.ssl.keystore.file")).findFirst().orElse("");
        String kp = Arrays.stream(args).filter(arg -> arg.contains("newsler.ssl.keystore.password")).findFirst().orElse("");
        String kt = Arrays.stream(args).filter(arg -> arg.contains("newsler.ssl.keystore.type")).findFirst().orElse("");
        String ka = Arrays.stream(args).filter(arg -> arg.contains("newsler.ssl.keystore.alias")).findFirst().orElse("");

        if (!StringUtils.isAllBlank(ks, kp, kt, ka)) {
            props.put("server.ssl.key-store", ks.split("=")[1]);
            props.put("server.ssl.key-store-password", kp.split("=")[1]);
            props.put("server.ssl.key-store-type", kt.split("=")[1]);
            props.put("server.ssl.key-alias", ka.split("=")[1]);
            props.put("server.ssl.enabled", "true");
        }

        new SpringApplicationBuilder(NewslerApplication.class)
                .properties(props)
                .run(args);
    }

    private static class NewslerInitializationException extends RuntimeException {
    }
}
