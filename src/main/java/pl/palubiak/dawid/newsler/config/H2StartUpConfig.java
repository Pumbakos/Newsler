package pl.palubiak.dawid.newsler.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.businesclinet.model.EmailType;
import pl.palubiak.dawid.newsler.businesclinet.repository.BusinessClientRepository;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.user.model.UserRole;
import pl.palubiak.dawid.newsler.user.service.UserRegistrationService;
import pl.palubiak.dawid.newsler.user.model.requestmodel.UserRequest;
import pl.palubiak.dawid.newsler.user.repository.UserRepository;
import pl.palubiak.dawid.newsler.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class H2StartUpConfig {
    @Bean
    CommandLineRunner saveUsers(UserRegistrationService service, UserService userService) {
        return args -> {
            User user1 = new User();
            user1.setName("Dave");
            user1.setLastName("Pumbakos");
            user1.setAppKey(UUID.randomUUID().toString());
            user1.setSecretKey(UUID.randomUUID().toString());
            user1.setSmtpAccount("1.pumbakos.smtp");
            user1.setEmail("dave@newsletter.io");
            user1.setPassword("jnasfiuasb");
            user1.setRole(UserRole.USER);

            User user2 = new User();
            user2.setName("Ejs");
            user2.setLastName("Aizholat");
            user2.setAppKey(UUID.randomUUID().toString());
            user2.setSecretKey(UUID.randomUUID().toString());
            user2.setSmtpAccount("1.aizholat.smtp");
            user2.setEmail("ejs.aizholat@newsletter.io");
            user2.setPassword("kamsfoasf1");
            user2.setRole(UserRole.USER);

            User user3 = new User();
            user3.setName("Anton");
            user3.setLastName("Newbie");
            user3.setAppKey(UUID.randomUUID().toString());
            user3.setSecretKey(UUID.randomUUID().toString());
            user3.setSmtpAccount("1.newbie.smtp");
            user3.setEmail("newbie@newsletter.io");
            user3.setPassword("askf1m09f3m41");
            user3.setRole(UserRole.USER);

            service.register(new UserRequest(
                            user1.getName(),
                            user1.getLastName(),
                            user1.getPassword(),
                            user1.getEmail(),
                            user1.getAppKey(),
                            user1.getSecretKey(),
                            user1.getSmtpAccount()
                    )
            );
            service.register(new UserRequest(
                            user2.getName(),
                            user2.getLastName(),
                            user2.getPassword(),
                            user2.getEmail(),
                            user2.getAppKey(),
                            user2.getSecretKey(),
                            user2.getSmtpAccount()
                    )
            );
            service.register(new UserRequest(
                            user3.getName(),
                            user3.getLastName(),
                            user3.getPassword(),
                            user3.getEmail(),
                            user3.getAppKey(),
                            user3.getSecretKey(),
                            user3.getSmtpAccount()
                    )
            );
        };
    }

    @Bean
    CommandLineRunner saveBusinessClients(BusinessClientRepository repository, UserRepository userRepository) {
        return args -> {
            BusinessClient b1 = new BusinessClient();
            b1.setEmail("test@java.com");
            b1.setName("Test");
            b1.setLastName("Testified");
            b1.setEmailType(EmailType.ALL);

            Optional<User> optionalUser1 = userRepository.findById(1L);
            assert optionalUser1.isPresent();
            b1.setUser(optionalUser1.get());

            BusinessClient b2 = new BusinessClient();
            b2.setEmail("ejs@aizholat.com");
            b2.setName("Ejs");
            b2.setLastName("Aizholat");
            b2.setEmailType(EmailType.NEWSLETTER);

            Optional<User> optionalUser2 = userRepository.findById(1L);
            assert optionalUser2.isPresent();
            b2.setUser(optionalUser2.get());

            BusinessClient b3 = new BusinessClient();
            b3.setEmail("max@aizholat.com");
            b3.setName("Max");
            b3.setLastName("Aizholat");
            b3.setEmailType(EmailType.NEWSLETTER);

            Optional<User> optionalUser3 = userRepository.findById(2L);
            assert optionalUser3.isPresent();
            b3.setUser(optionalUser3.get());

            BusinessClient b4 = new BusinessClient();
            b4.setEmail("ksi@aizholat.com");
            b4.setName("Ksi");
            b4.setLastName("Newman");
            b4.setEmailType(EmailType.PARTNERSHIP_OFFER);

            Optional<User> optionalUser4 = userRepository.findById(2L);
            assert optionalUser4.isPresent();
            b4.setUser(optionalUser4.get());

            BusinessClient b5 = new BusinessClient();
            b5.setEmail("andrew@newsler.com");
            b5.setName("Andrew");
            b5.setLastName("Smith");
            b5.setEmailType(EmailType.ALL);

            Optional<User> optionalUser5 = userRepository.findById(3L);
            assert optionalUser5.isPresent();
            b5.setUser(optionalUser5.get());

            BusinessClient b6 = new BusinessClient();
            b6.setEmail("nathan@aizholat.com");
            b6.setName("Nathan");
            b6.setLastName("Landlord");
            b6.setEmailType(EmailType.PARTNERSHIP_OFFER);

            Optional<User> optionalUser6 = userRepository.findById(3L);
            assert optionalUser6.isPresent();
            b6.setUser(optionalUser6.get());

            repository.saveAll(List.of(b1, b2, b3, b4, b5, b6));
        };
    }
}
