package pl.newsler.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface NLIPasswordEncoder {
    BCryptPasswordEncoder bCrypt();
    String encrypt(String string, AlgorithmType algorithm);

    String decrypt(String string, AlgorithmType algorithm);
}
