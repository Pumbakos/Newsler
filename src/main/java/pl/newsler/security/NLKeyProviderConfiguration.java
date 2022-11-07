package pl.newsler.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
class NLKeyProviderConfiguration {
    @Bean
    public NLIKeyProvider keyProvider() {
        return new NLKeyProvider();
    }
}
