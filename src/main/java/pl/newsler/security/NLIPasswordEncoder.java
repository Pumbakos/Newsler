package pl.newsler.security;

public interface NLIPasswordEncoder {
    String encrypt(String string, AlgorithmType algorithm);

    String decrypt(String string, AlgorithmType algorithm);
}
