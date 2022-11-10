package pl.newsler.testcommons;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUserUtils {
    private static final Faker faker = new Faker();

    public static String domain() {
        return faker.app().name().replace(" ", ".").replace("'", ".");
    }

    public static String username() {
        return faker.name().username().replace(" ", ".").replace("'", ".");
    }

    public static String lastName() {
        return faker.name().lastName().replace(" ", ".").replace("'", ".");
    }

    public static String firstName() {
        return faker.name().firstName().replace(" ", ".").replace("'", ".");
    }

    public static String secretOrAppKey() {
        return faker.regexify("[a-zA-Z0-9]{40}");
    }

    public static String smtpAccount() {
        return faker.regexify("[0-9]{1}[.]{1}[a-z]{3,}[.]{1}smtp");
    }
}
