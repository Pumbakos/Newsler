package pl.newsler.components.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.api.IUserController;
import pl.newsler.commons.exception.GlobalRestExceptionHandler;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.components.user.dto.UserGetRequest;
import pl.newsler.security.StubNLPasswordEncoder;

public class UserControllerTest {
    private final GlobalRestExceptionHandler handler = new GlobalRestExceptionHandler();
    private final TestUserFactory factory = new TestUserFactory();
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubUserRepository userRepositoryMock = new StubUserRepository();
    private final UserModuleConfiguration configuration = new UserModuleConfiguration(
            userRepositoryMock,
            passwordEncoder
    );
    private final IUserCrudService service = configuration.userService();
    private final IUserController controller = new UserController(service);

    @BeforeEach
    void beforeEach() {
        factory.standard().setId(
                service.create(
                        NLFirstName.of(factory.standard().getFirstName().getValue()),
                        NLLastName.of(factory.standard().getLastName().getValue()),
                        NLEmail.of(factory.standard().getEmail().getValue()),
                        NLPassword.of(factory.standard().getNLPassword().getValue())
                ));
        factory.dashed().setId(
                service.create(
                        NLFirstName.of(factory.dashed().getFirstName().getValue()),
                        NLLastName.of(factory.dashed().getLastName().getValue()),
                        NLEmail.of(factory.dashed().getEmail().getValue()),
                        NLPassword.of(factory.dashed().getNLPassword().getValue())
                ));
        factory.dotted().setId(
                service.create(
                        NLFirstName.of(factory.dotted().getFirstName().getValue()),
                        NLLastName.of(factory.dotted().getLastName().getValue()),
                        NLEmail.of(factory.dotted().getEmail().getValue()),
                        NLPassword.of(factory.dotted().getNLPassword().getValue())
                ));
    }

    @AfterEach
    void afterEach() {
        userRepositoryMock.deleteAll();
    }


    @Test
    void shouldGetUserWhenValidRequest() {
        NLUser user = factory.dashed();
        UserGetRequest request = new UserGetRequest(user.getEmail().getValue(), user.getPassword());
        ResponseEntity<NLDUser> response = controller.get(request);

        Assertions.assertNotNull(request);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
