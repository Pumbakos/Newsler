package pl.palubiak.dawid.newsler.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import pl.palubiak.dawid.newsler.generators.UserGenerator;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.user.service.UserService;

@ContextConfiguration(classes = UserServiceTest.class)
@SpringBootTest
public class UserServiceTest {
    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Should return user's instance if user was created")
    public void saveValidUserData(){
        User user = UserGenerator.createNewbieUser();
        Mockito.when(userService.save(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(user);
        User result = userService.save(user.getEmail(), user.getName(), user.getPassword());

        Mockito.verify(userService, Mockito.times(1)).save(user.getEmail(), user.getName(), user.getPassword());
        Mockito.verifyNoMoreInteractions(userService);

        Assertions.assertEquals(result, user);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if user's email is blank")
    public void saveInvalidUserDataEmail(){
        User user = UserGenerator.createNewbieUser();
        Mockito.when(userService.save(Mockito.nullable(String.class), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.save(null, user.getName(), user.getPassword()));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if user's name is blank")
    public void saveInvalidUserDataName(){
        User user = UserGenerator.createNewbieUser();
        Mockito.when(userService.save(Mockito.anyString(), Mockito.nullable(String.class), Mockito.anyString()))
                .thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.save(user.getName(), null, user.getPassword()));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if user's password is blank")
    public void saveInvalidUserDataPassword(){
        User user = UserGenerator.createNewbieUser();
        Mockito.when(userService.save(Mockito.anyString(), Mockito.anyString(), Mockito.nullable(String.class)))
                .thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.save(user.getEmail(), user.getName(), null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if all user's data is blank")
    public void saveInvalidUserDataAll(){
        User user = UserGenerator.createNewbieUser();
        Mockito.when(userService.save(Mockito.nullable(String.class), Mockito.nullable(String.class), Mockito.nullable(String.class)))
                .thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.save(null, null,null));
    }

    @Test
    @DisplayName("Should throw NPE if user's id is null")
    public void updateUserDataWithNullId(){
        User user = UserGenerator.createUser();
        Mockito.when(userService.update(Mockito.nullable(Long.class), Mockito.any(User.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(NullPointerException.class, () -> userService.update((Long) null, user));
    }

    @Test
    @DisplayName("Should return true if user was updated")
    public void updateValidUserDataWithValidId(){
        User user = UserGenerator.createUser();
        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(User.class))).thenReturn(true);

        Assertions.assertTrue(userService.update(1, user));
    }

    @Test
    @DisplayName("Should return false if user was not found")
    public void updateValidUserDataWithInvalidId(){
        User user = UserGenerator.createUser();
        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(User.class))).thenReturn(false);

        Assertions.assertFalse(userService.update(1, user));
    }

    @Test
    @DisplayName("Should return false if user's data is invalid")
    public void updateInvalidUserDataWithValidId(){
        User user = UserGenerator.createBlankUser();
        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(User.class))).thenReturn(false);

        Assertions.assertFalse(userService.update(1, user));
    }

    @Test
    @DisplayName("Should return false if user's was not found and data is invalid")
    public void updateInvalidUserDataWithInvalidId(){
        User user = UserGenerator.createBlankUser();
        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(User.class))).thenReturn(false);

        Assertions.assertFalse(userService.update(1, user));
    }

    @Test
    @DisplayName("Should return true if user was deleted")
    public void deleteUserWithValidId(){
        Mockito.when(userService.delete(Mockito.anyLong())).thenReturn(true);

        Assertions.assertTrue(userService.delete(1));
    }

    @Test
    @DisplayName("Should return false if user was not found")
    public void deleteUserWithInvalidId(){
        Mockito.when(userService.delete(Mockito.anyLong())).thenReturn(false);

        Assertions.assertFalse(userService.delete(1));
    }

    @Test
    @DisplayName("Should return true if client was added")
    public void addBusinessClient(){
        User user = UserGenerator.createUser();
        Mockito.when(userService.addBusinessClient(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);

        Assertions.assertTrue(userService.addBusinessClient(1, user.getEmail(), user.getName()));
    }

    @Test
    @DisplayName("Should return true if client was added")
    public void addBusinessClientUserNotFound(){
        Mockito.when(userService.addBusinessClient(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.addBusinessClient(1, "email", "name"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if client's email and name is blank")
    public void addBusinessClientWithBlankEmailAndName(){
        Mockito.when(userService.addBusinessClient(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.addBusinessClient(1, "", ""));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if user was not found")
    public void donNotAddBusinessClientCuzClientNotFound(){
        Mockito.when(userService.addBusinessClient(Mockito.anyLong(), Mockito.nullable(String.class), Mockito.nullable(String.class)))
                .thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.addBusinessClient(1, "", ""));
    }
}
