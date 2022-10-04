package pl.newsler.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.UUID;

@Configuration
public class H2StartUpConfig {
    @Bean
    CommandLineRunner saveUsers(UserService service) {
        return args -> {
            User user1 = new User.UserBuilder()
                    .name("New")
                    .lastName("Pumbakos")
                    .appKey(UUID.randomUUID().toString())
                    .secretKey(UUID.randomUUID().toString())
                    .smtpAccount("1.pumbakos.smtp")
                    .email("dave@newsletter.io")
                    .password("root")
                    .role(UserRole.ADMIN)
                    .enabled(true)
                    .locked(false)
                    .build();

            User user2 = new User.UserBuilder()
                    .name("Ejs")
                    .lastName("Aizholat")
                    .appKey(UUID.randomUUID().toString())
                    .secretKey(UUID.randomUUID().toString())
                    .smtpAccount("1.aizholat.smtp")
                    .email("ejs.aizholat@newsletter.io")
                    .password("kamsfoasf1")
                    .role(UserRole.USER)
                    .enabled(true)
                    .locked(false)
                    .build();

            User user3 = new User.UserBuilder()
                    .name("Anton")
                    .lastName("Newbie")
                    .appKey(UUID.randomUUID().toString())
                    .secretKey(UUID.randomUUID().toString())
                    .smtpAccount("1.newbie.smtp")
                    .email("newbie@newsletter.io")
                    .password("askf1m09f3m41")
                    .role(UserRole.USER)
                    .enabled(true)
                    .locked(false)
                    .build();

            service.register(Arrays.asList(user1, user2, user3));
        };
    }
}
