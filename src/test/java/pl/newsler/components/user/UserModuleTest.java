package pl.newsler.components.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.newsler.api.exceptions.UserDataNotFineException;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.security.StubNLPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static pl.newsler.testcommons.TestUserUtils.secretOrAppKey;

@SuppressWarnings("java:S5778")// none of `of()` methods listed below throws any Exception
class UserModuleTest {
    private final StubNLPasswordEncoder passwordEncoderConfigurationMock =
            new StubNLPasswordEncoder();
    private final StubUserRepository userRepositoryMock = new StubUserRepository();
    private final UserModuleConfiguration configuration = new UserModuleConfiguration(
            userRepositoryMock,
            passwordEncoderConfigurationMock.passwordEncoder()
    );
    private final IUserCrudService service = configuration.userService();
    private final TestUserFactory factory = new TestUserFactory();

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
    }


    /* ------------------ GET USER ----------------- */
    @Test
    void shouldGetUserById() {
        Assertions.assertNotNull(service.get(factory.standard().getId()));
        Assertions.assertNotNull(service.get(factory.dashed().getId()));
        Assertions.assertNotNull(service.get(factory.dotted().getId()));
        Assertions.assertDoesNotThrow(() -> service.get(factory.standard().getId()));
        Assertions.assertDoesNotThrow(() -> service.get(factory.dashed().getId()));
        Assertions.assertDoesNotThrow(() -> service.get(factory.dotted().getId()));

        NLDUser standard = service.get(factory.standard().getId());
        NLDUser dashed = service.get(factory.dashed().getId());
        NLDUser dotted = service.get(factory.dotted().getId());
        Assertions.assertTrue(standard.isEnabled());
        Assertions.assertTrue(dashed.isEnabled());
        Assertions.assertTrue(dotted.isEnabled());
    }

    @Test
    void shouldNotGetUserById_ThrowUserDataNotFineException() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.get(null));
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.get(NLUuid.of(UUID.randomUUID())));
    }

    /* ---------------- CREATE USER ---------------- */
    @Test
    void shouldCreateNewUser() {
        NLUuid first = service.create(
                NLFirstName.of("meal"),
                NLLastName.of("serve"),
                NLEmail.of("organ@person.dev"),
                NLPassword.of("Pa$$word7hat^match3$")
        );

        NLUuid second = service.create(
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
        final NLUuid standardId = factory.standard().getId();
        final String appKey = secretOrAppKey();
        final String secretKey = secretOrAppKey();

        Assertions.assertDoesNotThrow(
                () -> service.update(
                        standardId
                ));

        final Optional<NLUser> optionalNLUser = userRepositoryMock.findById(standardId);
        if (optionalNLUser.isEmpty()) {
            Assertions.fail();
        }
        final NLUser user = optionalNLUser.get();
        Assertions.assertEquals(passwordEncoderConfigurationMock.encrypt(appKey), user.getAppKey().getValue());
        Assertions.assertEquals(passwordEncoderConfigurationMock.encrypt(secretKey), user.getSecretKey().getValue());
    }

    @Test
    void shouldNotUpdateExistingUser_BlankData() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                factory.standard().getId()
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                factory.dotted().getId()
        ));
    }

    @Test
    void shouldNotUpdateExistingUser_RegexesDoNotMatches() {
        final NLUuid standardUserId = factory.standard().getId();
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                standardUserId
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                standardUserId
        ));

        final NLUuid dotedUserId = factory.dotted().getId();
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                dotedUserId
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                dotedUserId
        ));

        final NLUuid dashedUserId = factory.dashed().getId();
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                dashedUserId
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                dashedUserId
        ));

        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                dashedUserId
        ));
    }

    @Test
    void shouldNotUpdate_NonExistingUser_ValidData() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.update(
                NLUuid.of(UUID.randomUUID())
        ));
    }

    /* ---------------- DELETE USER ---------------- */
    @Test
    void shouldDeleteUser_CorrectIdAndPassword() {
        final NLUuid standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalNLUser = userRepositoryMock.findById(standardUserId);
        if (optionalNLUser.isEmpty()) {
            Assertions.fail();
        }
        Assertions.assertDoesNotThrow(() -> service.delete(standardUserId, factory.standard().getNLPassword()));
        Assertions.assertEquals(Optional.empty(), userRepositoryMock.findById(standardUserId));
    }

    @Test
    void shouldNotDeleteUser_IncorrectId_CorrectPassword() {
        final NLUuid standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalNLUser = userRepositoryMock.findById(standardUserId);
        if (optionalNLUser.isEmpty()) {
            Assertions.fail();
        }
        final NLUser user = optionalNLUser.get();

        Assertions.assertEquals(standardUserId, user.getId());
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.delete(null, NLPassword.of(user.getPassword())));
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.delete(NLUuid.of(UUID.randomUUID()), factory.standard().getNLPassword()));
        Assertions.assertEquals(optionalNLUser, userRepositoryMock.findById(standardUserId));
    }

    @Test
    void shouldNotDeleteUser_CorrectId_IncorrectPassword() {
        final NLUuid standardUserId = factory.standard().getId();
        final Optional<NLUser> optionalNLUser = userRepositoryMock.findById(standardUserId);
        if (optionalNLUser.isEmpty()) {
            Assertions.fail();
        }
        final NLUser user = optionalNLUser.get();

        Assertions.assertEquals(standardUserId, user.getId());
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.delete(user.getId(), NLPassword.of(factory.standard_plainPassword())));
        Assertions.assertEquals(optionalNLUser, userRepositoryMock.findById(standardUserId));
    }

    @Test
    void shouldCompareNLUser() {
        final NLUser standardUser = factory.standard();
        final NLUser dottedUser = factory.dotted();
        NLDUser nldStandardUser = NLDUser.of(standardUser);
        NLDUser nldDottedUser = NLDUser.of(dottedUser);

        Assertions.assertEquals(nldStandardUser, NLDUser.of(standardUser));
        Assertions.assertEquals(nldStandardUser.toString(), nldStandardUser.toString());
        Assertions.assertEquals(nldStandardUser.hashCode(), nldStandardUser.hashCode());
        Assertions.assertNotEquals(nldStandardUser, nldDottedUser);
        Assertions.assertNotEquals(nldStandardUser.toString(), nldDottedUser.toString());
        Assertions.assertNotEquals(nldStandardUser.hashCode(), nldDottedUser.hashCode());
        Assertions.assertNotEquals(nldStandardUser, nldDottedUser);
    }

    @Test
    void shouldGetNLUserProperties() {
        final NLUser standardUser = factory.standard();
        standardUser.setAppKey(NLAppKey.of(secretOrAppKey()));
        standardUser.setSecretKey(NLSecretKey.of(secretOrAppKey()));
        standardUser.setSmtpAccount(NLSmtpAccount.of("1.test.smp"));

        Assertions.assertNotNull(standardUser);
        Assertions.assertNotNull(standardUser.getId());
        Assertions.assertNotNull(standardUser.getEmail());
        Assertions.assertNotNull(standardUser.getFirstName());
        Assertions.assertNotNull(standardUser.getLastName());
        Assertions.assertNotNull(standardUser.getNLPassword());
        Assertions.assertNotNull(standardUser.getSmtpAccount());
        Assertions.assertNotNull(standardUser.getSecretKey());
        Assertions.assertNotNull(standardUser.getAppKey());
        Assertions.assertNotNull(standardUser.getRole());
        Assertions.assertNotNull(standardUser.getAuthorities());
        Assertions.assertTrue(standardUser.getAuthorities().contains(new SimpleGrantedAuthority(standardUser.getRole().name())));
        Assertions.assertTrue(standardUser.isEnabled());
        Assertions.assertTrue(standardUser.isCredentialsNonExpired());
        Assertions.assertTrue(standardUser.isAccountNonExpired());
        Assertions.assertTrue(standardUser.isAccountNonLocked());
    }

    @Test
    void shouldCompareNLDUser() {
        final NLUser standardUser = factory.standard();
        final NLUser dottedUser = factory.dotted();

        Assertions.assertEquals(standardUser, factory.standard());
        Assertions.assertEquals(standardUser.toString(), standardUser.toString());
        Assertions.assertEquals(standardUser.hashCode(), standardUser.hashCode());
        Assertions.assertNotEquals(standardUser, NLDUser.of(dottedUser));
        Assertions.assertNotEquals(standardUser, null);
        Assertions.assertNotEquals(standardUser, dottedUser);
        Assertions.assertNotEquals(standardUser.toString(), dottedUser.toString());
        Assertions.assertNotEquals(standardUser.hashCode(), dottedUser.hashCode());
        Assertions.assertNotEquals(standardUser, dottedUser);
    }

    @Test
    void shouldGetNLDUserProperties() {
        final NLUser standardUser = factory.standard();

        NLDUser nldUser = NLDUser.of(standardUser);
        Assertions.assertNotNull(nldUser);
        Assertions.assertNotNull(nldUser.toString());
        Assertions.assertEquals(standardUser.getId(), nldUser.getId());
        Assertions.assertEquals(standardUser.getEmail(), nldUser.getEmail());
        Assertions.assertEquals(standardUser.getFirstName(), nldUser.getName());
        Assertions.assertEquals(standardUser.getLastName(), nldUser.getLastName());
        Assertions.assertEquals(standardUser.getNLPassword(), nldUser.getPassword());
        Assertions.assertEquals(standardUser.getSmtpAccount(), nldUser.getSmtpAccount());
        Assertions.assertEquals(standardUser.getSecretKey(), nldUser.getSecretKey());
        Assertions.assertEquals(standardUser.getAppKey(), nldUser.getAppKey());
        Assertions.assertEquals(standardUser.getRole(), nldUser.getRole());
        Assertions.assertEquals(standardUser.isEnabled(), nldUser.isEnabled());
        Assertions.assertEquals(standardUser.isCredentialsNonExpired(), nldUser.isCredentialsNonExpired());
        Assertions.assertEquals(nldUser.toString(), NLDUser.of(standardUser).toString());
    }
}
