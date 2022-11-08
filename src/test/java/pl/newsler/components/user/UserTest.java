package pl.newsler.components.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.security.MockNLIKeyProviderConfiguration;
import pl.newsler.security.MockNLPasswordEncoderConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pl.newsler.components.user.UserTestUtils.domain;
import static pl.newsler.components.user.UserTestUtils.firstName;
import static pl.newsler.components.user.UserTestUtils.lastName;
import static pl.newsler.components.user.UserTestUtils.secretOrAppKey;
import static pl.newsler.components.user.UserTestUtils.smtpAccount;
import static pl.newsler.components.user.UserTestUtils.username;

class UserTest {
    private final List<NLId> ids = new ArrayList<>();
    private final MockNLPasswordEncoderConfiguration passwordEncoderConfigurationMock =
            new MockNLPasswordEncoderConfiguration(new MockNLIKeyProviderConfiguration());
    private final MockUserRepository userRepositoryMock = new MockUserRepository();
    private final UserConfiguration configuration = new UserConfiguration(
            userRepositoryMock,
            passwordEncoderConfigurationMock.passwordEncoder(),
            passwordEncoderConfigurationMock.bCryptPasswordEncoder()
    );
    private final IUserService service = configuration.userService();

    @BeforeEach
    void beforeEach() {
        ids.addAll(List.of(
                service.create(
                        NLFirstName.of(firstName()),
                        NLLastName.of(lastName()),
                        NLEmail.of(String.format("%s@%s.dev", username(), username())),
                        NLPassword.of("Pa$$word7hat^match3$")
                ),
                service.create(
                        NLFirstName.of(firstName()),
                        NLLastName.of(lastName()),
                        NLEmail.of(String.format("%s@%s.dev", firstName(), domain())),
                        NLPassword.of("U$Adna923masdmi")
                ),
                service.create(
                        NLFirstName.of(firstName()),
                        NLLastName.of(lastName()),
                        NLEmail.of(String.format("%s@%s.dev", firstName(), domain())),
                        NLPassword.of("UJkds81#@^dsa")
                )
        ));
    }

    @AfterEach
    void afterEach() {
        userRepositoryMock.deleteAll();
        ids.clear();
    }

    @Test
    void shouldGetUserById() {
        Assertions.assertNotNull(service.getById(ids.get(0)));
        Assertions.assertNotNull(service.getById(ids.get(1)));
        Assertions.assertNotNull(service.getById(ids.get(2)));
        Assertions.assertDoesNotThrow(() -> service.getById(ids.get(0)));
        Assertions.assertDoesNotThrow(() -> service.getById(ids.get(1)));
        Assertions.assertDoesNotThrow(() -> service.getById(ids.get(2)));
    }

    @SuppressWarnings("java:S5778")// none of `of()` methods listed below throws any Exception
    @Test
    void shouldNotGetUserById_Throw() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.getById(NLId.of(UUID.randomUUID())));
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.getById(NLId.of(UUID.randomUUID())));
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.getById(NLId.of(UUID.randomUUID())));
    }

    @Test
    void shouldCreateNewUser() {
        NLId first = service.create(
                NLFirstName.of("meal"),
                NLLastName.of("serve"),
                NLEmail.of("organ@person.dev"),
                NLPassword.of("Pa$$word7hat^match3$")
        );

        NLId second = service.create(
                NLFirstName.of("ruin"),
                NLLastName.of("whistle"),
                NLEmail.of("fence.actual@person.dev.ai"),
                NLPassword.of("Ma77ching94$$wor3")
        );

        Assertions.assertNotNull(first);
        Assertions.assertTrue(first.getValue().startsWith("usr_"));
        Assertions.assertNotNull(second);
        Assertions.assertTrue(second.getValue().startsWith("usr_"));

        Assertions.assertDoesNotThrow(() -> service.create(
                NLFirstName.of("ruin"),
                NLLastName.of("whistle"),
                NLEmail.of("fence.actual@person.dev.ai"),
                NLPassword.of("Ma77ching94$$wor3")
        ));

        Assertions.assertDoesNotThrow(() -> service.create(
                NLFirstName.of("meal"),
                NLLastName.of("serve"),
                NLEmail.of("organ@person.dev"),
                NLPassword.of("Pa$$word7hat^match3$")
        ));
    }

    @SuppressWarnings("java:S5778")// none of `of()` methods listed below throws any Exception
    @Test
    void shouldNotCreateNewUser_BlankData() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.create(
                NLFirstName.of(""),
                NLLastName.of(""),
                NLEmail.of(""),
                NLPassword.of("")
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.create(
                NLFirstName.of(null),
                NLLastName.of(null),
                NLEmail.of(null),
                NLPassword.of(null)
        ));
    }

    @SuppressWarnings("java:S5778")// none of `of()` methods listed below throws any Exception
    @Test
    void shouldNotCreateNewUser_PasswordDoesNotMatchRegex() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.create(
                NLFirstName.of("meal"),
                NLLastName.of("serve-hesitate"),
                NLEmail.of("organ@person.dev"),
                NLPassword.of(null)
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.create(
                NLFirstName.of("meal"),
                NLLastName.of("serve-hesitate"),
                NLEmail.of("organ@person.dev"),
                NLPassword.of("")
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.create(
                NLFirstName.of("meal"),
                NLLastName.of("serve-hesitate"),
                NLEmail.of("organ@person.dev"),
                NLPassword.of("")
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.create(
                NLFirstName.of("meal"),
                NLLastName.of("serve-hesitate"),
                NLEmail.of("organ@person.dev"),
                NLPassword.of("abd123idasmdaw")
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.create(
                NLFirstName.of("meal"),
                NLLastName.of("serve-hesitate"),
                NLEmail.of("organ@person.dev"),
                NLPassword.of("c8c498fd-8b88-4aac-a9d5-8eab3353e93e")
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.create(
                NLFirstName.of("meal"),
                NLLastName.of("serve-hesitate"),
                NLEmail.of("organ@person.dev"),
                NLPassword.of("12letterpassword")
        ));
    }

    @Test
    void shouldUpdateExistingUser() {
        boolean updated = service.update(
                ids.get(0),
                NLAppKey.of(secretOrAppKey()),
                NLSecretKey.of(secretOrAppKey()),
                NLSmtpAccount.of(smtpAccount())
        );

        Assertions.assertTrue(updated);
    }
}
