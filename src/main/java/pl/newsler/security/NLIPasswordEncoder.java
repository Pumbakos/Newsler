package pl.newsler.security;

import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface NLIPasswordEncoder {
    BCryptPasswordEncoder bCrypt();

    String encrypt(@NotNull String string);

    String decrypt(@NotNull String string);
}
