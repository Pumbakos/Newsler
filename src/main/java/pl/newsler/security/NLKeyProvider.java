package pl.newsler.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class NLKeyProvider implements NLIKeyProvider {
    @Override
    public byte[] getKey(NLPublicAlias alias) {
        return NLKeyStore.getKey(alias);
    }

    @Override
    @SuppressWarnings("java:S2129")
    public char[] getCharKey(NLPublicAlias alias) {
        return new String(NLKeyStore.getKey(alias)).toCharArray();
    }
}
