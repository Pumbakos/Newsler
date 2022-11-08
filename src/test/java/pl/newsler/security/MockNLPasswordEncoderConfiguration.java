package pl.newsler.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MockNLPasswordEncoderConfiguration implements NLIPasswordEncoder {
    private final NLPasswordEncoderConfiguration configuration;

    public MockNLPasswordEncoderConfiguration(MockNLIKeyProviderConfiguration keyProviderConfigurationMock) {
        this.configuration = new NLPasswordEncoderConfiguration(keyProviderConfigurationMock);
    }

    public NLPasswordEncoder passwordEncoder() {
        return configuration.passwordEncoder(configuration.bCryptPasswordEncoder());
    }

    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return configuration.bCryptPasswordEncoder();
    }

    @Override
    public BCryptPasswordEncoder bCrypt() {
        return configuration.bCryptPasswordEncoder();
    }

    @Override
    public String encrypt(String string, AlgorithmType algorithm) {
        return configuration.passwordEncoder(bCrypt()).encrypt(string, algorithm);
    }

    @Override
    public String decrypt(String string, AlgorithmType algorithm) {
        return configuration.passwordEncoder(bCrypt()).decrypt(string, algorithm);
    }
}