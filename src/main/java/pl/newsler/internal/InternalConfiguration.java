package pl.newsler.internal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class InternalConfiguration {
    @Bean(name = "domainProperties")
    NewslerServiceProperties domainProperties() {
        return new NewslerServiceProperties();
    }
}
