package pl.palubiak.dawid.newsler.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.businesclinet.repository.BusinessClientRepository;

import java.util.List;

@Configuration
public class H2StartUpConfig {
    @Bean
    CommandLineRunner userCommandLineRunner(BusinessClientRepository repository) {
        return args -> {
            BusinessClient b1 = new BusinessClient();
            b1.setEmail("dave@aizholat.com");
            b1.setName("Dave");
            b1.setLastName("Pumbakos");
            b1.setActive(true);
            b1.setActiveNewsLetters(true);
            b1.setActivePartnershipOffers(true);

            BusinessClient b2 = new BusinessClient();
            b2.setEmail("ejs@aizholat.com");
            b2.setName("Ejs");
            b2.setLastName("aizholat");
            b2.setActive(true);
            b2.setActiveNewsLetters(true);
            b2.setActivePartnershipOffers(true);

            repository.saveAll(List.of(b1, b2));
        };
    }
}
