package pl.newsler.testcommons.environment;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class EnvironmentVariablesPropsStrategy implements StubEnvironmentCreationStrategy {
    @Override
    public void create(final Map<String, String> map) {
        final String absolutePath = new File(Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("keystore/newslertest.p12")
        ).getFile()).getAbsolutePath();

        map.put("newsler.service.schema", "http");
        map.put("newsler.service.port", "8080");
        map.put("newsler.service.domain-name", "localhost");
        map.put("newsler.designer.schema", "http");
        map.put("newsler.designer.port", "4200");
        map.put("newsler.designer.domain-name", "localhost");
        map.put("newsler.security.keystore.key-store-type", "PKCS12");
        map.put("newsler.security.keystore.key-store-path", absolutePath);
        map.put("newsler.security.keystore.key-store-password", "4u85oGTKF5nX3cw#2j!My*gP");
        map.put("newsler.security.keystore.key-alias", "newslertest");
        map.put("newsler.security.keystore.protection-password-phrase", "W*o^vmeJwkFAkAt4nMFFwKhp^q%3nM7@LW6K@u^2xYTDr#cgv4jteJw#HCQ#jaSj");
        map.put("newsler.security.keystore.encode-key-salt", "y@2rpNzhJLux*azxPzikzBKjSDEKnuJ%RYtLTfdfwqkxtSqQfLmiqFBgfZKPLwjz");
    }
}
