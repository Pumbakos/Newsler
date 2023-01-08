package pl.newsler.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface NLIPasswordEncoder {
    BCryptPasswordEncoder bCrypt();

    String encrypt(String string);

    String decrypt(String string);
}
