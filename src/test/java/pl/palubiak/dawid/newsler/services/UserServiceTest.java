//package pl.palubiak.dawid.newsler.services;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ContextConfiguration;
//import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
//import pl.palubiak.dawid.newsler.generators.BusinessClientGenerator;
//import pl.palubiak.dawid.newsler.generators.UserGenerator;
//import pl.palubiak.dawid.newsler.user.model.User;
//import pl.palubiak.dawid.newsler.user.model.requestmodel.RequestUser;
//import pl.palubiak.dawid.newsler.user.service.UserService;
//
//import java.util.Optional;
//
//@ContextConfiguration(classes = UserServiceTest.class)
//@SpringBootTest
//public class UserServiceTest {
//    @MockBean
//    private UserService userService;
//
//    @Test
//    @DisplayName("Should return user's instance if user was created")
//    public void saveValidUserData(){
//        RequestUser simpleUserModel = UserGenerator.createSimpleUserModel();
//        User user = UserGenerator.createUser();
//        Mockito.when(userService.save(Mockito.any(RequestUser.class))).thenReturn(Optional.of(user));
//        Optional<User> result = userService.save(simpleUserModel);
//
//        assert result.isPresent();
//
//        Assertions.assertEquals(result.get().getEmail(), user.getEmail());
//        Assertions.assertEquals(result.get().getName(), user.getName());
//        Assertions.assertEquals(result.get().getPassword(), user.getPassword());
//    }
//
//    @Test
//    @DisplayName("Should return Optional.empty() if user was not valid")
//    public void saveInvalidUserData(){
//        RequestUser simpleUserModel = UserGenerator.createSimpleUserModel();
//        User user = UserGenerator.createNewbieUser();
//        Mockito.when(userService.save(Mockito.any(RequestUser.class))).thenReturn(Optional.empty());
//        Optional<User> result = userService.save(simpleUserModel);
//
//        Assertions.assertEquals(Optional.empty(), result);
//    }
//
//    @Test
//    @DisplayName("Should throw NPE if user's id is null")
//    public void updateUserDataWithNullId(){
//        User user = UserGenerator.createUser();
//        Mockito.when(userService.update(Mockito.nullable(Long.class), Mockito.any(User.class))).thenThrow(NullPointerException.class);
//
//        Assertions.assertThrows(NullPointerException.class, () -> userService.update((Long) null, user));
//    }
//
//    @Test
//    @DisplayName("Should return true if user was updated")
//    public void updateValidUserDataWithValidId(){
//        User user = UserGenerator.createUser();
//        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(User.class))).thenReturn(true);
//
//        Assertions.assertTrue(userService.update(1, user));
//    }
//
//    @Test
//    @DisplayName("Should return false if user was not found")
//    public void updateValidUserDataWithInvalidId(){
//        User user = UserGenerator.createUser();
//        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(User.class))).thenReturn(false);
//
//        Assertions.assertFalse(userService.update(1, user));
//    }
//
//    @Test
//    @DisplayName("Should return false if user's data is invalid")
//    public void updateInvalidUserDataWithValidId(){
//        User user = UserGenerator.createBlankUser();
//        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(User.class))).thenReturn(false);
//
//        Assertions.assertFalse(userService.update(1, user));
//    }
//
//    @Test
//    @DisplayName("Should return false if user's was not found and data is invalid")
//    public void updateInvalidUserDataWithInvalidId(){
//        User user = UserGenerator.createBlankUser();
//        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(User.class))).thenReturn(false);
//
//        Assertions.assertFalse(userService.update(1, user));
//    }
//
//    @Test
//    @DisplayName("Should return true if user was deleted")
//    public void deleteUserWithValidId(){
//        Mockito.when(userService.delete(Mockito.anyLong())).thenReturn(true);
//
//        Assertions.assertTrue(userService.delete(1));
//    }
//
//    @Test
//    @DisplayName("Should return false if user was not found")
//    public void deleteUserWithInvalidId(){
//        Mockito.when(userService.delete(Mockito.anyLong())).thenReturn(false);
//
//        Assertions.assertFalse(userService.delete(1));
//    }
//
//    @Test
//    @DisplayName("Should return true if client was added")
//    public void addBusinessClient(){
//        BusinessClient businessClientSimpleModel = BusinessClientGenerator.createBusinessClient();
//        Mockito.when(userService.addBusinessClient(Mockito.anyLong(), Mockito.any(BusinessClient.class)))
//                .thenReturn(true);
//
//        Assertions.assertTrue(userService.addBusinessClient(1, businessClientSimpleModel));
//    }
//    @Test
//    @DisplayName("Should return false if client was not added")
//    public void doNotAddBusinessClient(){
//        BusinessClient businessClientSimpleModel = BusinessClientGenerator.createBusinessClient();
//        Mockito.when(userService.addBusinessClient(Mockito.anyLong(), Mockito.any(BusinessClient.class)))
//                .thenReturn(false);
//
//        Assertions.assertFalse(userService.addBusinessClient(1, businessClientSimpleModel));
//    }
//
//    @Test
//    @DisplayName("Should throw IllegalArgumentException if client was not found added")
//    public void addBusinessClientUserNotFound(){
//        BusinessClient businessClientSimpleModel = BusinessClientGenerator.createBusinessClient();
//        Mockito.when(userService.addBusinessClient(Mockito.anyLong(), Mockito.any(BusinessClient.class)))
//                .thenThrow(IllegalArgumentException.class);
//
//        Assertions.assertThrows(IllegalArgumentException.class,
//                () -> userService.addBusinessClient(1, businessClientSimpleModel));
//    }
//
//    @Test
//    @DisplayName("Should throw IllegalArgumentException if client's email and name is blank")
//    public void addBusinessClientWithBlankEmailAndName(){
//        BusinessClient businessClientSimpleModel = BusinessClientGenerator.createBusinessClient();
//        Mockito.when(userService.addBusinessClient(Mockito.anyLong(), Mockito.any(BusinessClient.class)))
//                .thenThrow(IllegalArgumentException.class);
//
//        Assertions.assertThrows(IllegalArgumentException.class,
//                () -> userService.addBusinessClient(1, businessClientSimpleModel));
//    }
//}
