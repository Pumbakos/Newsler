package pl.newsler.devenv;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.newsler.components.emaillabs.usecase.ELAInstantMailRequest;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class H2Util {
    private static final Faker faker = new Faker();
    private static final Random random = new SecureRandom();

    static String domain() {
        return faker
                .app()
                .name()
                .replace(" ", ".")
                .replace("'", ".")
                .toLowerCase();
    }

    static String username() {
        return faker
                .name()
                .username()
                .replace(" ", ".")
                .replace("'", ".")
                .toLowerCase();
    }

    static String lastName() {
        String lastName = faker
                .name()
                .lastName()
                .replace(" ", ".")
                .replace("'", ".");
        return lastName.substring(0, 1).concat(lastName.substring(1));
    }

    static String firstName() {
        String firstName = faker
                .name()
                .firstName()
                .replace(" ", ".")
                .replace("'", ".");
        return firstName.substring(0, 1).concat(firstName.substring(1));
    }

    static String fullEmail() {
        return String.format(String.format("%s@%s.com", username(), domain()));
    }

    static String secretOrAppKey() {
        return faker.regexify("[a-zA-Z0-9]{40}");
    }

    static String smtpAccount() {
        return faker.regexify("[0-9]{1}[.]{1}[a-z]{3,}[.]{1}smtp");
    }

    static ELAInstantMailRequest createMailSendRequest(String userMail) {
        return new ELAInstantMailRequest(userMail, randomEmails(), faker.book().author(), randomMessage());
    }

    private static List<String> randomEmails() {
        int rand = random.nextInt(10);
        if (rand == 0) {
            return List.of(fullEmail(), fullEmail());
        }
        if (rand % 2 == 0) {
            return List.of(fullEmail(), fullEmail(), fullEmail(), fullEmail());
        }
        if (rand % 3 == 0) {
            return List.of(fullEmail(), fullEmail(), fullEmail(), fullEmail(), fullEmail(), fullEmail(), fullEmail());
        }

        return List.of(fullEmail(), fullEmail());
    }

    private static String randomMessage() {
        int rand = random.nextInt(15) + 10;
        final StringBuilder builder = new StringBuilder();
        if (rand % 2 == 0) {
            for (int i = 0; i < rand; i++) {
                builder.append(faker.name().fullName()).append(" ")
                        .append(faker.app().name()).append(" ")
                        .append(faker.esports().team()).append(" ");
            }
        } else if (rand % 3 == 0) {
            builder.append(faker.friends().quote()).append(" ")
                    .append(faker.gameOfThrones().quote()).append(" ")
                    .append(faker.chuckNorris().fact()).append(" ")
                    .append(faker.rickAndMorty().quote()).append(" ")
                    .append(faker.harryPotter().quote()).append(" ");
        } else {
            builder.append(faker.lebowski().quote()).append(" ")
                    .append(faker.elderScrolls().quote()).append(" ")
                    .append(faker.music().genre()).append(" ")
                    .append(faker.twinPeaks().quote()).append(" ")
                    .append(faker.backToTheFuture().quote()).append(" ");
        }

        return builder.length() > 1_000 ? builder.substring(0, 1_000) : builder.toString();
    }
}
