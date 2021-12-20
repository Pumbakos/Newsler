package pl.palubiak.dawid.newsler.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.businesclinet.model.EmailType;
import pl.palubiak.dawid.newsler.businesclinet.repository.BusinessClientRepository;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.user.repository.UserRepository;

import java.util.List;

@Configuration
public class H2StartUpConfig {
    @Bean
    CommandLineRunner userCommandLineRunner(UserRepository repository) {
        return args -> {
            User user1 = new User();
            user1.setName("Dave");
            user1.setLastName("Pumbakos");
            user1.setEmail("dave@newsletter.io");
            user1.setPassword("jnasfiuasb");

            User user2 = new User();
            user2.setName("Ejs");
            user2.setLastName("Aizholat");
            user2.setEmail("ejs.aizholat@newsletter.io");
            user2.setPassword("kamsfoasf1");

            repository.saveAll(List.of(user1, user2));
        };
    }

    @Bean
    CommandLineRunner businessClientCommandLineRunner(BusinessClientRepository repository, UserRepository userRepository) {
        return args -> {
            BusinessClient b1 = new BusinessClient();
            b1.setEmail("dave@aizholat.com");
            b1.setName("Dave");
            b1.setLastName("Pumbakos");
            b1.setEmailType(EmailType.ALL);
            b1.setUser(userRepository.findById(1L).get());

            BusinessClient b2 = new BusinessClient();
            b2.setEmail("ejs@aizholat.com");
            b2.setName("Ejs");
            b2.setLastName("aizholat");
            b2.setEmailType(EmailType.NEWSLETTER);
            b2.setUser(userRepository.findById(2L).get());

            repository.saveAll(List.of(b1, b2));
        };
    }
}
