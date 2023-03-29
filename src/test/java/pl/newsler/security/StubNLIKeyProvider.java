package pl.newsler.security;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StubNLIKeyProvider implements NLIKeyProvider {
    private final NLKeyProviderConfiguration configuration = new NLKeyProviderConfiguration();

    @Override
    public byte[] getKey(String alias) {
        return configuration.keyProvider().getKey(alias);
    }

    @Override
    public char[] getCharKey(String alias) {
        return configuration.keyProvider().getCharKey(alias);
    }
}
