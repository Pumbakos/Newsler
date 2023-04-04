package pl.newsler.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.newsler.internal.exception.ConfigurationException;

@ComponentScan
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
class NLPasswordEncoderConfiguration {
    private final NLIKeyProvider keyProvider;
    private final Environment env;

    @Bean(name = "bCryptPasswordEncoder")
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 8);
    }

    @Bean(name = "passwordEncoder")
    NLPasswordEncoder passwordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        try {
            return new NLPasswordEncoder(bCryptPasswordEncoder, keyProvider, env);
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }
}
