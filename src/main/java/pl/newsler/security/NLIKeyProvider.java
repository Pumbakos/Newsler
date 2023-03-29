package pl.newsler.security;

public interface NLIKeyProvider {
    byte[] getKey(String alias);

    char[] getCharKey(String alias);
}