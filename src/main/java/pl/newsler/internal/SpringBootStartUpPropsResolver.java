package pl.newsler.internal;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Properties;

import static pl.newsler.internal.PropType.NEWSLER_SSL_KEYSTORE_ALIAS;
import static pl.newsler.internal.PropType.NEWSLER_SSL_KEYSTORE_FILE;
import static pl.newsler.internal.PropType.NEWSLER_SSL_KEYSTORE_PASSWORD;
import static pl.newsler.internal.PropType.NEWSLER_SSL_KEYSTORE_TYPE;
import static pl.newsler.internal.PropType.SERVER_PORT;
import static pl.newsler.internal.PropType.SERVER_SSL_ENABLED;
import static pl.newsler.internal.PropType.SERVER_SSL_KEYSTORE_ALIAS;
import static pl.newsler.internal.PropType.SERVER_SSL_KEYSTORE_FILE;
import static pl.newsler.internal.PropType.SERVER_SSL_KEYSTORE_PASSWORD;
import static pl.newsler.internal.PropType.SERVER_SSL_KEYSTORE_TYPE;

/**
 * Set up security properties {@link PropType}. Promotes CLI args over environmental variables
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpringBootStartUpPropsResolver {
    private static final Properties props = new Properties();

    public static Properties resolve(@NotNull final String[] args) {
        final String ksSysEnv = getenv(NEWSLER_SSL_KEYSTORE_FILE);
        final String kpSysEnv = getenv(NEWSLER_SSL_KEYSTORE_PASSWORD);
        final String ktSysEnv = getenv(NEWSLER_SSL_KEYSTORE_TYPE);
        final String kaSysEnv = getenv(NEWSLER_SSL_KEYSTORE_ALIAS);

        final String ksArg = Arrays.stream(args).filter(arg -> arg.contains(NEWSLER_SSL_KEYSTORE_FILE.value())).findFirst().orElse("");
        final String kpArg = Arrays.stream(args).filter(arg -> arg.contains(NEWSLER_SSL_KEYSTORE_PASSWORD.value())).findFirst().orElse("");
        final String ktArg = Arrays.stream(args).filter(arg -> arg.contains(NEWSLER_SSL_KEYSTORE_TYPE.value())).findFirst().orElse("");
        final String kaArg = Arrays.stream(args).filter(arg -> arg.contains(NEWSLER_SSL_KEYSTORE_ALIAS.value())).findFirst().orElse("");

        if (!StringUtils.isAllEmpty(ksArg, kpArg, ktArg, kaArg)) {
            resolveProperties(props, ksArg.split("=")[1], kpArg.split("=")[1], ktArg.split("=")[1], kaArg.split("=")[1]);
        } else if (!StringUtils.isAllEmpty(ksSysEnv, kpSysEnv, ktSysEnv, kaSysEnv)) {
            resolveProperties(props, ksSysEnv, kpSysEnv, ktSysEnv, kaSysEnv);
        } else {
            props.put(SERVER_SSL_ENABLED.value(), "false");
            props.put(SERVER_PORT.value(), "8080");
        }

        return props;
    }

    private static void resolveProperties(final Properties props, final String keyStoreFile, final String keyStorePassword,
                                          final String keyStoreType, final String keyStoreAlias) {
        props.put(SERVER_SSL_KEYSTORE_FILE.value(), keyStoreFile);
        props.put(SERVER_SSL_KEYSTORE_PASSWORD.value(), keyStorePassword);
        props.put(SERVER_SSL_KEYSTORE_TYPE.value(), keyStoreType);
        props.put(SERVER_SSL_KEYSTORE_ALIAS.value(), keyStoreAlias);
        props.put(SERVER_SSL_ENABLED.value(), "true");
        props.put(SERVER_PORT.value(), "8443");
    }

    private static String getenv(PropType prop) {
        try {
            return System.getenv(prop.value());
        } catch (Exception e) {
            return null;
        }
    }
}
