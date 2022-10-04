package pl.newsler.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

@Configuration
public class PasswordEncoder {
    private static final byte[] SALT = AesBytesEncryptor.CipherAlgorithm.GCM.defaultIvGenerator().generateKey();
    @Bean
    public BCryptPasswordEncoder bCrypt() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 8);
    }
}

