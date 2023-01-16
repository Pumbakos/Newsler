package pl.newsler.testcommons;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {
    private static final Faker faker = new Faker();

    public static String reqId() {
        return faker.regexify("[a-zA-Z0-9]{7}");
    }
}
