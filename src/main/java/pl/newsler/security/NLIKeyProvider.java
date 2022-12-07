package pl.newsler.security;

public interface NLIKeyProvider {
    byte[] getKey(NLPublicAlias alias);

    char[] getCharKey(NLPublicAlias alias);
}