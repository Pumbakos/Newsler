package pl.newsler.internal;

import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.org.webcompere.systemstubs.SystemStubs;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

import java.util.Properties;

@SpringBootTest()
class SpringBootStartUpPropsResolverTest {
    @Test
    void shouldResolveCLIArgs() {
        final String[] args = new String[4];
        args[0] = "newsler.ssl.keystore.file=FILE";
        args[1] = "newsler.ssl.keystore.password=PASSWORD";
        args[2] = "newsler.ssl.keystore.type=TYPE";
        args[3] = "newsler.ssl.keystore.alias=ALIAS";

        Properties props = SpringBootStartUpPropsResolver.resolve(args);
        Assertions.assertFalse(props.isEmpty());
        Assertions.assertEquals("FILE", props.get("server.ssl.key-store"));
        Assertions.assertEquals("PASSWORD", props.get("server.ssl.key-store-password"));
        Assertions.assertEquals("TYPE", props.get("server.ssl.key-store-type"));
        Assertions.assertEquals("ALIAS", props.get("server.ssl.key-alias"));
        Assertions.assertEquals("true", props.get("server.ssl.enabled"));
        Assertions.assertEquals("8443", props.get("server.ssl.port"));
    }

    @Test
    void shouldResolveSysEnvArgs() throws Exception {
        final String[] args = new String[0];
        SystemStubs
                .withEnvironmentVariable("newsler.ssl.keystore.file", "FILE")
                .and("newsler.ssl.keystore.password", "PASSWORD")
                .and("newsler.ssl.keystore.type", "TYPE")
                .and("newsler.ssl.keystore.alias", "ALIAS")
                .execute(() -> {
                    Properties props = SpringBootStartUpPropsResolver.resolve(args);
                    Assertions.assertFalse(props.isEmpty());
                    Assertions.assertEquals("FILE", props.get("server.ssl.key-store"));
                    Assertions.assertEquals("PASSWORD", props.get("server.ssl.key-store-password"));
                    Assertions.assertEquals("TYPE", props.get("server.ssl.key-store-type"));
                    Assertions.assertEquals("ALIAS", props.get("server.ssl.key-alias"));
                    Assertions.assertEquals("true", props.get("server.ssl.enabled"));
                    Assertions.assertEquals("8443", props.get("server.ssl.port"));
                });
    }

    @Test
    void shouldResolveDefaults() throws Exception {
        final String[] args = new String[0];
        Properties props = SpringBootStartUpPropsResolver.resolve(args);
        Assertions.assertEquals("false", props.get("server.ssl.enabled"));
        Assertions.assertEquals("8080", props.get("server.ssl.port"));
    }
}