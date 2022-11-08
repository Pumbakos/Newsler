package pl.newsler.devenv;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class H2Util {
    private static final Faker faker = new Faker();

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
        return faker
                .name()
                .lastName()
                .replace(" ", ".")
                .replace("'", ".")
                .toLowerCase();
    }

    static String firstName() {
        return faker
                .name()
                .firstName()
                .replace(" ", ".")
                .replace("'", ".")
                .toLowerCase();
    }

    static String secretOrAppKey() {
        return faker.regexify("[a-zA-Z0-9]{40}");
    }

    static String smtpAccount() {
        return faker.regexify("[0-9]{1}[.]{1}[a-z]{3,}[.]{1}smtp");
    }
}
