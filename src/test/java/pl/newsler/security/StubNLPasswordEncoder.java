package pl.newsler.security;

import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.newsler.testcommons.environment.KeyStorePropsStrategy;
import pl.newsler.testcommons.environment.StubEnvironment;

public class StubNLPasswordEncoder implements NLIPasswordEncoder {
    private final NLPasswordEncoderConfiguration configuration;
    private final Environment env = new StubEnvironment(new KeyStorePropsStrategy());

    public StubNLPasswordEncoder() {
        this.configuration = new NLPasswordEncoderConfiguration(new StubNLIKeyProvider(env), env);
    }

    @Override
    public BCryptPasswordEncoder bCrypt() {
        return configuration.bCryptPasswordEncoder();
    }

    @Override
    public String encrypt(String string) {
        return configuration.passwordEncoder(bCrypt()).encrypt(string);
    }

    @Override
    public String decrypt(String string) {
        return configuration.passwordEncoder(bCrypt()).decrypt(string);
    }
}
