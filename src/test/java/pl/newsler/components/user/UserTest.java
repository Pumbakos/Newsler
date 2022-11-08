package pl.newsler.components.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
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

import java.util.Optional;
import java.util.UUID;

import static pl.newsler.components.user.UserTestUtils.secretOrAppKey;
import static pl.newsler.components.user.UserTestUtils.smtpAccount;

@SuppressWarnings("java:S5778")// none of `of()` methods listed below throws any Exception
class UserTest {
    //    private final List<NLId> ids = new ArrayList<>();
    private final UserFactory factory = new UserFactory();
    private final MockNLPasswordEncoderConfiguration passwordEncoderConfigurationMock =
            new MockNLPasswordEncoderConfiguration(new MockNLIKeyProviderConfiguration());
    private final MockUserRepository userRepositoryMock = new MockUserRepository(passwordEncoderConfigurationMock.bCryptPasswordEncoder());
    private final UserConfiguration configuration = new UserConfiguration(
            userRepositoryMock,
            passwordEncoderConfigurationMock.passwordEncoder(),
            passwordEncoderConfigurationMock.bCryptPasswordEncoder()
    );
    private final IUserService service = configuration.userService();

    @BeforeEach
    void beforeEach() {
        factory.standard().setId(
                service.create(
                        factory.standard().getFirstName(),
                        factory.standard().getLastName(),
                        factory.standard().getEmail(),
                        factory.standard().getNLPassword()
                ));
        factory.dashed().setId(
                service.create(
                        factory.dashed().getFirstName(),
                        factory.dashed().getLastName(),
                        factory.dashed().getEmail(),
                        factory.dashed().getNLPassword()
                ));
        factory.dotted().setId(
                service.create(
                        factory.dotted().getFirstName(),
                        factory.dotted().getLastName(),
                        factory.dotted().getEmail(),
                        factory.dotted().getNLPassword()
                ));
    }

    @AfterEach
    void afterEach() {
        userRepositoryMock.deleteAll();
//        ids.clear();
    }


    /* ------------------ GET USER ----------------- */
    @Test
    void shouldGetUserById() {
        Assertions.assertNotNull(service.getById(factory.standard().getId()));
        Assertions.assertNotNull(service.getById(factory.dashed().getId()));
        Assertions.assertNotNull(service.getById(factory.dotted().getId()));
        Assertions.assertDoesNotThrow(() -> service.getById(factory.standard().getId()));
        Assertions.assertDoesNotThrow(() -> service.getById(factory.dashed().getId()));
        Assertions.assertDoesNotThrow(() -> service.getById(factory.dotted().getId()));
    }

    @Test
    void shouldNotGetUserById_ThrowUserDataNotFineException() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.getById(NLId.of(UUID.randomUUID())));
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.getById(NLId.of(UUID.randomUUID())));
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.getById(NLId.of(UUID.randomUUID())));
    }

    /* ---------------- CREATE USER ---------------- */
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

    /* ---------------- UPDATE USER ---------------- */
    @Test
    void shouldUpdateExistingUser_CorrectData() {
        Assertions.assertDoesNotThrow(
                () -> service.update(
                        factory.standard().getId(),
                        NLAppKey.of(secretOrAppKey()),
                        NLSecretKey.of(secretOrAppKey()),
                        NLSmtpAccount.of(smtpAccount())
                ));
    }

    @Test
    void shouldNotUpdateExistingUser_BlankData() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                factory.standard().getId(),
                NLAppKey.of(""),
                NLSecretKey.of(""),
                NLSmtpAccount.of("")
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                factory.dotted().getId(),
                NLAppKey.of(null),
                NLSecretKey.of(null),
                NLSmtpAccount.of(null)
        ));
    }

    @Test
    void shouldNotUpdateExistingUser_RegexesDoNotMatches() {
        final NLId standardUserId = factory.standard().getId();
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                standardUserId,
                NLAppKey.of("Da8ce3Yh42605144J38d2b73a4c6Baa89Ool31cc21kd"),
                NLSecretKey.of("Da8ce3Yh42605144J38d2b73a4c6Baa89Ool31cc21kd"),
                NLSmtpAccount.of("0.9wN.com")
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                standardUserId,
                NLAppKey.of("Da8ce3Yh42605144J38d2b73a4c6Baa89Ool31c"),
                NLSecretKey.of("Da8ce3Yh42605144J38d2b73a4c6Baa89Ool31c"),
                NLSmtpAccount.of("99.sma.smtp")
        ));

        final NLId dotedUserId = factory.dotted().getId();
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                dotedUserId,
                NLAppKey.of("dd7402d3-d538-486d-955c-2ac8a2ae482d-dgt"),
                NLSecretKey.of("ef855cca-8ce7-4494-a8f7-9050aa9757e0-psa"),
                NLSmtpAccount.of("-1.sma.smtp")
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                dotedUserId,
                NLAppKey.of("                 TRUE                   "),
                NLSecretKey.of("                 FALSE                  "),
                NLSmtpAccount.of("-1.DEV12.smtp")
        ));

        final NLId dashedUserId = factory.dashed().getId();
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                dashedUserId,
                NLAppKey.of(secretOrAppKey()),
                NLSecretKey.of("Da8ce3Yh42605144J38d2b73a4c6Baa89Ool31cc2a"),
                NLSmtpAccount.of("13.account.smtp")
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                dashedUserId,
                NLAppKey.of("Da8ce3Yh42605144J38d2b73a4c6Baa89Ool31cc2a"),
                NLSecretKey.of(secretOrAppKey()),
                NLSmtpAccount.of("13.account.smtp")
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                dashedUserId,
                NLAppKey.of("Da8ce3Yh42605144J38d2b73a4c6Baa89Ool31cc2a"),
                NLSecretKey.of("Da8ce3Yh42605144J38d2b73a4c6Baa89Ool31cc2a"),
                NLSmtpAccount.of(smtpAccount())
        ));
    }

    @Test
    void shouldNotUpdate_NonExistingUser_ValidData() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                NLId.of(UUID.randomUUID()),
                NLAppKey.of(secretOrAppKey()),
                NLSecretKey.of(secretOrAppKey()),
                NLSmtpAccount.of(smtpAccount())
        ));
    }

    /* ---------------- DELETE USER ---------------- */
    @Test
    void shouldDeleteUser_CorrectIdAndPassword() {
        final NLId standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalNLUser = userRepositoryMock.findById(standardUserId);
        if (optionalNLUser.isEmpty()) {
            Assertions.fail();
        }
        final NLUser user = optionalNLUser.get();
        Assertions.assertDoesNotThrow(() -> service.delete(standardUserId, factory.standard().getNLPassword()));
        Assertions.assertEquals(Optional.empty(), userRepositoryMock.findById(standardUserId));
    }

    @Test
    void shouldNotDeleteUser_IncorrectId_CorrectPassword() {
        final NLId standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalNLUser = userRepositoryMock.findById(standardUserId);
        if (optionalNLUser.isEmpty()) {
            Assertions.fail();
        }
        final NLUser user = optionalNLUser.get();

        Assertions.assertEquals(standardUserId, user.getId());
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.delete(null, NLPassword.of(user.getPassword())));
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.delete(NLId.of(UUID.randomUUID()), factory.standard().getNLPassword()));
        Assertions.assertEquals(optionalNLUser, userRepositoryMock.findById(standardUserId));
    }

    @Test
    void shouldNotDeleteUser_CorrectId_IncorrectPassword() {
        final NLId standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalNLUser = userRepositoryMock.findById(standardUserId);
        if (optionalNLUser.isEmpty()) {
            Assertions.fail();
        }
        final NLUser user = optionalNLUser.get();

        Assertions.assertEquals(standardUserId, user.getId());
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.delete(user.getId(), NLPassword.of("PL3ks81#@^dsa")));
        Assertions.assertEquals(optionalNLUser, userRepositoryMock.findById(standardUserId));
    }

    /* ----------------- LOAD USER ----------------- */
    @Test
    void shouldLoadUserByUsername_ValidEmail_ExistingUser() {
        final NLUser standardUser = factory.standard();
        Assertions.assertDoesNotThrow(() -> service.loadUserByUsername(standardUser.getEmail().getValue()));

        final Optional<NLUser> optionalNLUser = userRepositoryMock.findById(standardUser.getId());
        if (optionalNLUser.isEmpty()) {
            Assertions.fail();
        }

        final UserDetails userDetails = service.loadUserByUsername(standardUser.getEmail().getValue());
        Assertions.assertEquals(userDetails.getUsername(), standardUser.getUsername());
        Assertions.assertEquals(userDetails.getPassword(), optionalNLUser.get().getPassword());
    }

    @Test
    void shouldNotLoadUserByUsername_InvalidEmail() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.loadUserByUsername("user.d'amora@person.dev"));
    }

    @Test
    void shouldNotLoadUserByUsername_ValidEmail_NonExistingUser() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.loadUserByUsername("user.d-amora@person.dev"));
    }
}
