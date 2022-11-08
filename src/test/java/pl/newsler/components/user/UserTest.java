package pl.newsler.components.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.security.MockNLIKeyProviderConfiguration;
import pl.newsler.security.MockNLPasswordEncoderConfiguration;

class UserTest {
    private final MockNLIKeyProviderConfiguration keyProviderConfigurationMock = new MockNLIKeyProviderConfiguration();
    private final MockNLPasswordEncoderConfiguration passwordEncoderConfigurationMock = new MockNLPasswordEncoderConfiguration(keyProviderConfigurationMock);
    private final UserConfiguration configuration = new UserConfiguration(
            new MockUserRepository(),
            passwordEncoderConfigurationMock.passwordEncoder(),
            passwordEncoderConfigurationMock.bCryptPasswordEncoder()
    );
    private final IUserService service = configuration.userService();

    /* ---------------- CREATE USER ---------------- */
    @Test
    void shouldCreateNewUser() {
        NLId id = service.create(
                NLFirstName.of("meal"),
                NLLastName.of("serve"),
                NLEmail.of("organ@person.dev"),
                NLPassword.of("U$Adna923masdmi")
        );

        Assertions.assertNotNull(id);
        Assertions.assertTrue(id.getValue().startsWith("usr_"));
        Assertions.assertDoesNotThrow(() -> service.create(
                NLFirstName.of("meal"),
                NLLastName.of("serve-hesitate"),
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
}
