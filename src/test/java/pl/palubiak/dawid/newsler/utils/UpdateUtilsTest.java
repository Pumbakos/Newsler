package pl.palubiak.dawid.newsler.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ContextConfiguration;
import pl.palubiak.dawid.newsler.generators.UserGenerator;
import pl.palubiak.dawid.newsler.user.model.User;

@ContextConfiguration(classes = {UpdateUtils.class, User.class})
@SpringBootTest
public class UpdateUtilsTest {
    @MockBean
    private UpdateUtils<User> updateUtils;

    @MockBean
    private JpaRepository<User, Long> userRepository;

    @Test
    @DisplayName("Should update user's fields")
    public void updateUserData() {
        User user = UserGenerator.createUser();
        Mockito.when(updateUtils.update(userRepository, user, 1L)).thenReturn(true);
        boolean result = updateUtils.update(userRepository, user, 1L);
        Mockito.verify(updateUtils).update(userRepository, user, 1L);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Should nit update user's fields")
    public void dontUpdateUserData() {
        User user = UserGenerator.createUser();
        Mockito.when(updateUtils.update(userRepository, user, 1L)).thenReturn(false);
        boolean result = updateUtils.update(userRepository, user, 1L);
        Mockito.verify(updateUtils).update(userRepository, user, 1L);

        Assertions.assertFalse(result);
    }
}
