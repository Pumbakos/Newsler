package pl.palubiak.dawid.newsler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pl.palubiak.dawid.newsler.generators.UserGenerator;
import pl.palubiak.dawid.newsler.user.model.requestmodel.RequestUser;

@SpringBootTest
public class RequestUserTest {
    @Test
    @DisplayName("Should return false if user model is not valid")
    public void shouldNotCreateUserSimpleModel() {
        RequestUser requestUser = UserGenerator.createInvalidSimpleUserModel();
        Assertions.assertFalse(requestUser.isValid());
    }

    @Test
    @DisplayName("Should return true if user model is valid")
    public void shouldCreateUserSimpleModel() {
        RequestUser requestUser = UserGenerator.createSimpleUserModel();
        Assertions.assertTrue(requestUser.isValid());
    }
}
