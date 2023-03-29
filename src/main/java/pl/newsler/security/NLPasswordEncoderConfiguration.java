package pl.newsler.security;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ComponentScan
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
class NLPasswordEncoderConfiguration {
    private final NLIKeyProvider keyProvider;

    @Bean(name = "bCryptPasswordEncoder")
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 8);
    }

    @Bean(name = "passwordEncoder")
    NLPasswordEncoder passwordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return new NLPasswordEncoder(bCryptPasswordEncoder, keyProvider);
    }
}
