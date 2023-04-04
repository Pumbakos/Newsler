package pl.newsler.testcommons.environment;

import java.util.Map;

public class KeyStorePropsStrategy implements StubEnvironmentCreationStrategy {
    @Override
    public void create(final Map<String, String> map) {
        new EnvironmentVariablesPropsStrategy().create(map);
        map.put("newsler.security.keystore.app-key-alias", "newsler_test_app_key_alias");
        map.put("newsler.security.keystore.secret-key-alias", "newsler_test_secret_key_alias");
        map.put("newsler.security.keystore.smtp-alias", "newsler_test_smtp_alias");
        map.put("newsler.security.keystore.email-alias", "newsler_test_email_alias");
    }
}
