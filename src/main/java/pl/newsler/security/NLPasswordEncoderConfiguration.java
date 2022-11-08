package pl.newsler.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
class NLPasswordEncoderConfiguration {
    private final NLIKeyProvider keyProvider;

    @Bean
    public NLPasswordEncoder passwordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return new NLPasswordEncoder(bCryptPasswordEncoder, keyProvider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 8);
    }
}
