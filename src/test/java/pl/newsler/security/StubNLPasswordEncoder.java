package pl.newsler.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class StubNLPasswordEncoder implements NLIPasswordEncoder {
    private final NLPasswordEncoderConfiguration configuration;

    public StubNLPasswordEncoder() {
        this.configuration = new NLPasswordEncoderConfiguration();
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
