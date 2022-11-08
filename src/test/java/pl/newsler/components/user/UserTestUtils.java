package pl.newsler.components.user;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTestUtils {
    private static final Faker faker = new Faker();

    static String domain() {
        return faker.app().name().replace(" ", ".").replace("'", ".");
    }

    static String username() {
        return faker.name().username().replace(" ", ".").replace("'", ".");
    }

    static String lastName() {
        return faker.name().lastName().replace(" ", ".").replace("'", ".");
    }

    static String firstName() {
        return faker.name().firstName().replace(" ", ".").replace("'", ".");
    }

    static String secretOrAppKey() {
        return faker.regexify("[a-zA-Z0-9]{40}");
    }

    static String smtpAccount() {
        return faker.regexify("[0-9]{1}[.]{1}[a-z]{3,}[.]{1}smtp");
    }
}
