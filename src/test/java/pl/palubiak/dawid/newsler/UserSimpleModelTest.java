package pl.palubiak.dawid.newsler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pl.palubiak.dawid.newsler.generators.UserGenerator;
import pl.palubiak.dawid.newsler.user.model.UserSimpleModel;

@SpringBootTest
public class UserSimpleModelTest {
    @Test
    @DisplayName("Should return false if user model is not valid")
    public void shouldNotCreateUserSimpleModel() {
        UserSimpleModel userSimpleModel = UserGenerator.createInvalidSimpleUserModel();
        Assertions.assertFalse(userSimpleModel.isValid());
    }

    @Test
    @DisplayName("Should return true if user model is valid")
    public void shouldCreateUserSimpleModel() {
        UserSimpleModel userSimpleModel = UserGenerator.createSimpleUserModel();
        Assertions.assertTrue(userSimpleModel.isValid());
    }
}
