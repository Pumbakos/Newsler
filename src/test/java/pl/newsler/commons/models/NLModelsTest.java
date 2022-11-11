package pl.newsler.commons.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import pl.newsler.exceptions.RegexNotMatchException;
import pl.newsler.testcommons.TestUserUtils;

import java.util.UUID;

import static pl.newsler.testcommons.TestUserUtils.domain;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.lastName;
import static pl.newsler.testcommons.TestUserUtils.secretOrAppKey;
import static pl.newsler.testcommons.TestUserUtils.username;

@SuppressWarnings({"java:S5778"})
class NLModelsTest {
    //* ---------- APP KEY & SECRET KEY ---------- *//
    @Test
    void shouldCreatePackagePrivateNLAppKeyAndNLSecretKey() {
        Assertions.assertDoesNotThrow(NLSecretKey::new);
        Assertions.assertDoesNotThrow(NLSecretKey::new);
        Assertions.assertDoesNotThrow(NLSecretKey::new);
        Assertions.assertDoesNotThrow(NLAppKey::new);
        Assertions.assertDoesNotThrow(NLAppKey::new);
        Assertions.assertDoesNotThrow(NLAppKey::new);
    }

    @Test
    void shouldValidateNLAppKeyAndNLSecretKey() {
        Assertions.assertTrue(NLAppKey.of(TestUserUtils.secretOrAppKey()).validate());
        Assertions.assertTrue(NLSecretKey.of(TestUserUtils.secretOrAppKey()).validate());
    }

    @Test
    void shouldCompareNLAppKeysAndNLSecretKeys() {
        String secretOrAppKey = secretOrAppKey();

        Assertions.assertTrue(NLAppKey.of(secretOrAppKey).canEqual(NLAppKey.of(secretOrAppKey)));
        Assertions.assertFalse(NLAppKey.of(secretOrAppKey).canEqual(NLSecretKey.of(secretOrAppKey)));
        Assertions.assertEquals(NLAppKey.of(secretOrAppKey), NLAppKey.of(secretOrAppKey));
        Assertions.assertEquals(NLAppKey.of(secretOrAppKey).toString(), NLAppKey.of(secretOrAppKey).toString());
        Assertions.assertEquals(NLAppKey.of(secretOrAppKey).hashCode(), NLAppKey.of(secretOrAppKey).hashCode());
        Assertions.assertNotEquals(NLAppKey.of(secretOrAppKey()), NLAppKey.of(secretOrAppKey()));

        Assertions.assertTrue(NLSecretKey.of(secretOrAppKey).canEqual(NLSecretKey.of(secretOrAppKey)));
        Assertions.assertFalse(NLSecretKey.of(secretOrAppKey).canEqual(NLAppKey.of(secretOrAppKey)));
        Assertions.assertEquals(NLSecretKey.of(secretOrAppKey), NLSecretKey.of(secretOrAppKey));
        Assertions.assertEquals(NLSecretKey.of(secretOrAppKey).toString(), NLSecretKey.of(secretOrAppKey).toString());
        Assertions.assertEquals(NLSecretKey.of(secretOrAppKey).hashCode(), NLSecretKey.of(secretOrAppKey).hashCode());
        Assertions.assertNotEquals(NLSecretKey.of(secretOrAppKey()), NLSecretKey.of(secretOrAppKey()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "HI91s3J5ub6HmPwH2z8c2lpA2Hhl9cNA7Ogm9gD7f",
            "dYyBiWgWJ4R6DL4W0E0KCP6N8rX2IX5nfZ9eRB1",
            "dYyBiWgWJ4R6DL4W0E0KCP6N8rX2IX5nfZ9eRB1$",
            ""
    })
    void shouldNotValidateNLAppKeyAndNLSecretKey_RegexDoesNotMatch(String key) {
        Assertions.assertFalse(NLAppKey.of(key).validate());
        Assertions.assertFalse(NLSecretKey.of(key).validate());
    }

    @Test
    void shouldNotValidateNLAppKeyAndNLSecretKey_NullKeys() {
        Assertions.assertFalse(NLAppKey.of(null).validate());
        Assertions.assertFalse(NLSecretKey.of(null).validate());
    }

    //* ---------- NLEmail ---------- *//
    @Test
    void shouldCreatePackagePrivateNLEmail() {
        Assertions.assertDoesNotThrow(NLEmail::new);
        Assertions.assertDoesNotThrow(NLEmail::new);
        Assertions.assertDoesNotThrow(NLEmail::new);
    }

    @Test
    void shouldValidateNLEmail() {
        Assertions.assertTrue(NLEmail.of(String.format("%s@%s.dev", firstName(), domain())).validate());
        Assertions.assertTrue(NLEmail.of(String.format("%s@%s.dev", username(), domain())).validate());
        Assertions.assertTrue(NLEmail.of(String.format("%s-%s@%s.dev", firstName(), lastName(), domain())).validate());
    }

    @Test
    void shouldNotValidateNLEmail_RegexDoesNotMatchAndBlankValue() {
        Assertions.assertFalse(NLEmail.of("test'user@app.co").validate());
        Assertions.assertFalse(NLEmail.of("").validate());
        Assertions.assertFalse(NLEmail.of(null).validate());
    }

    @Test
    void shouldCompareNLEmails() {
        String email = String.format("%s@%s.dev", username(), domain());

        Assertions.assertTrue(NLEmail.of(email).canEqual(NLEmail.of(email)));
        Assertions.assertFalse(NLEmail.of(email).canEqual(email));
        Assertions.assertEquals(NLEmail.of(email), NLEmail.of(email));
        Assertions.assertEquals(NLEmail.of(email).toString(), NLEmail.of(email).toString());
        Assertions.assertEquals(NLEmail.of(email).hashCode(), NLEmail.of(email).hashCode());
        Assertions.assertNotEquals(
                NLEmail.of(String.format("%s@%s.dev", username(), domain())),
                NLEmail.of(String.format("%s@%s.dev", username(), domain()))
        );
    }

    //* ---------- NLId ---------- *//
    @Test
    void shouldCreatePackagePrivateNLId() {
        Assertions.assertDoesNotThrow(NLId::new);
        Assertions.assertDoesNotThrow(NLId::new);
        Assertions.assertDoesNotThrow(NLId::new);
    }

    @Test
    void shouldValidateNLId() {
        UUID uuid = UUID.randomUUID();
        NLId first = NLId.of(uuid);

        Assertions.assertTrue(first.validate());
        Assertions.assertEquals(uuid.toString(), first.getValue().substring(4));
        Assertions.assertTrue(first.getValue().startsWith("usr_"));
    }

    @ParameterizedTest
    @EnumSource(value = NLType.class)
    void shouldValidateNLIdByNLType(NLType type) {
        UUID uuid = UUID.randomUUID();
        NLId id = NLId.of(uuid, type);
        Assertions.assertTrue(id.validate());
        Assertions.assertTrue(id.getValue().startsWith(type.getPrefix()));
        Assertions.assertEquals(uuid.toString(), id.getValue().substring(4));
    }

    @Test
    void shouldNotValidateNLId_NullType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NLId.of(UUID.randomUUID(), null));
    }

    @Test
    void shouldCompareNLIds() {
        UUID uuid = UUID.randomUUID();

        Assertions.assertTrue(NLId.of(uuid).canEqual(NLId.of(uuid)));
        Assertions.assertFalse(NLId.of(uuid).canEqual(uuid));
        Assertions.assertEquals(NLId.of(uuid), NLId.of(uuid));
        Assertions.assertEquals(NLId.of(uuid).toString(), NLId.of(uuid).toString());
        Assertions.assertEquals(NLId.of(uuid).hashCode(), NLId.of(uuid).hashCode());
        Assertions.assertNotEquals(NLId.of(UUID.randomUUID()), NLId.of(UUID.randomUUID()));
    }

    @Test
    void shouldThrowIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NLId.of(UUID.randomUUID(), NLType.valueOf("TEST_USER")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> NLId.of(UUID.fromString("")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> NLId.of(UUID.fromString("user")));
    }

    //* ---------- NLPassword ---------- *//
    @Test
    void shouldCreatePackagePrivateNLPassword() {
        Assertions.assertDoesNotThrow(NLPassword::new);
        Assertions.assertDoesNotThrow(NLPassword::new);
        Assertions.assertDoesNotThrow(NLPassword::new);
    }

    @Test
    void shouldValidateNLPassword() {
        Assertions.assertTrue(NLPassword.of("Pa$$word7hat^match3$").validate());
        Assertions.assertTrue(NLPassword.of("UJk6ds81#@^dsa").validate());
        Assertions.assertTrue(NLPassword.of("U$Ad3na923mas$dmi").validate());
    }

    @Test
    void shouldNotValidateNLPassword_RegexDoesNotMatch() {
        Assertions.assertFalse(NLPassword.of("").validate());
        Assertions.assertFalse(NLPassword.of(null).validate());
        Assertions.assertFalse(NLPassword.of("or7O6rVUKZuN2b").validate());
    }

    @Test
    void shouldCompareNLPassword() {
        String password = "U$Ad3na923mas$dmi";

        Assertions.assertTrue(NLPassword.of(password).canEqual(NLPassword.of(password)));
        Assertions.assertFalse(NLPassword.of(password).canEqual(password));
        Assertions.assertEquals(NLPassword.of(password), NLPassword.of(password));
        Assertions.assertEquals(NLPassword.of(password).toString(), NLPassword.of(password).toString());
        Assertions.assertEquals(NLPassword.of(password).hashCode(), NLPassword.of(password).hashCode());
        Assertions.assertNotEquals(NLPassword.of("UJk6ds81#@^dsa"), NLPassword.of("Pa$$word7hat^match3$"));
    }

    //* ---------- NLFirstName ---------- *//
    @Test
    void shouldCreatePackagePrivateNLFirstName() {
        Assertions.assertDoesNotThrow(NLFirstName::new);
        Assertions.assertDoesNotThrow(NLFirstName::new);
        Assertions.assertDoesNotThrow(NLFirstName::new);
    }

    @Test
    void shouldValidateNLFirstName() {
        Assertions.assertTrue(NLFirstName.of("Tina").validate());
        Assertions.assertTrue(NLFirstName.of("Oz").validate());
        Assertions.assertTrue(NLFirstName.of("Oze").validate());
        Assertions.assertTrue(NLFirstName.of("TheXenotransplantatioon").validate());
    }

    @Test
    void shouldNotValidateNLFirstName_RegexDoesNotMatch() {
        Assertions.assertFalse(NLFirstName.of("").validate());
        Assertions.assertFalse(NLFirstName.of("Hippopotomonstrosesquippedaliophobia").validate());
        Assertions.assertFalse(NLFirstName.of(null).validate());
    }

    @Test
    void shouldCompareNLFirstName() {
        String firstName = "Newsler";

        Assertions.assertTrue(NLFirstName.of(firstName).canEqual(NLFirstName.of(firstName)));
        Assertions.assertFalse(NLFirstName.of(firstName).canEqual(firstName));
        Assertions.assertEquals(NLFirstName.of(firstName), NLFirstName.of(firstName));
        Assertions.assertEquals(NLFirstName.of(firstName).toString(), NLFirstName.of(firstName).toString());
        Assertions.assertEquals(NLFirstName.of(firstName).hashCode(), NLFirstName.of(firstName).hashCode());
        Assertions.assertNotEquals(NLFirstName.of("Newsler"), NLFirstName.of("Newslertest"));
    }

    //* ---------- NLLastName ---------- *//
    @Test
    void shouldCreatePackagePrivateNLLastName(){
        Assertions.assertDoesNotThrow(NLLastName::new);
        Assertions.assertDoesNotThrow(NLLastName::new);
        Assertions.assertDoesNotThrow(NLLastName::new);
    }

    @Test
    void shouldValidateNLLastName() {
        Assertions.assertTrue(NLLastName.of("Tina").validate());
        Assertions.assertTrue(NLLastName.of("Oz").validate());
        Assertions.assertTrue(NLLastName.of("Oze").validate());
        Assertions.assertTrue(NLLastName.of("The Xenotransplantatioo").validate());
        Assertions.assertTrue(NLLastName.of("The'Xenotransplantatioo").validate());
        Assertions.assertTrue(NLLastName.of("The.Xenotransplantatioo").validate());
        Assertions.assertTrue(NLLastName.of("The,Xenotransplantatioo").validate());
    }

    @Test
    void shouldNotValidateNLLastName_RegexDoesNotMatch() {
        Assertions.assertFalse(NLLastName.of("").validate());
        Assertions.assertFalse(NLLastName.of("Hippopotomonstrosesquippedaliophobia").validate());
        Assertions.assertFalse(NLLastName.of(null).validate());
    }

    @Test
    void shouldCompareNLLastName() {
        String lastName = "Newsler";

        Assertions.assertTrue(NLLastName.of(lastName).canEqual(NLLastName.of(lastName)));
        Assertions.assertFalse(NLLastName.of(lastName).canEqual(lastName));
        Assertions.assertEquals(NLLastName.of(lastName), NLLastName.of(lastName));
        Assertions.assertEquals(NLLastName.of(lastName).toString(), NLLastName.of(lastName).toString());
        Assertions.assertEquals(NLLastName.of(lastName).hashCode(), NLLastName.of(lastName).hashCode());
        Assertions.assertNotEquals(NLLastName.of("Newsler"), NLLastName.of("Newslertest"));
    }

    //* ---------- NLSmtpAccount ---------- *//
    @Test
    void shouldCreatePackagePrivateNLSmtpAccount() {
        Assertions.assertDoesNotThrow(NLSmtpAccount::new);
        Assertions.assertDoesNotThrow(NLSmtpAccount::new);
        Assertions.assertDoesNotThrow(NLSmtpAccount::new);
    }

    @Test
    void shouldValidateNLSmtpAccount() {
        Assertions.assertTrue(NLSmtpAccount.of("3.test.smtp").validate());
        Assertions.assertTrue(NLSmtpAccount.of("1.testcase.smtp").validate());
        Assertions.assertTrue(NLSmtpAccount.of("2.testcasescenario.smtp").validate());
    }

    @Test
    void shouldNotValidateNLSmtpAccount_RegexDoesNotMatch() {
        Assertions.assertFalse(NLSmtpAccount.of("3.test.smtp.com").validate());
        Assertions.assertFalse(NLSmtpAccount.of("1.testcase.smt").validate());
        Assertions.assertFalse(NLSmtpAccount.of("22.testcasescenario.smtp").validate());
        Assertions.assertFalse(NLSmtpAccount.of("").validate());
        Assertions.assertFalse(NLSmtpAccount.of(null).validate());
    }

    @Test
    void shouldCompareNLSmtpAccount() {
        String smtp = "3.test.smtp";

        Assertions.assertTrue(NLSmtpAccount.of(smtp).canEqual(NLSmtpAccount.of(smtp)));
        Assertions.assertFalse(NLSmtpAccount.of(smtp).canEqual(smtp));
        Assertions.assertEquals(NLSmtpAccount.of(smtp), NLSmtpAccount.of(smtp));
        Assertions.assertEquals(NLSmtpAccount.of(smtp).toString(), NLSmtpAccount.of(smtp).toString());
        Assertions.assertEquals(NLSmtpAccount.of(smtp).hashCode(), NLSmtpAccount.of(smtp).hashCode());
        Assertions.assertNotEquals(NLSmtpAccount.of("3.test.smtp"), NLSmtpAccount.of("1.testcase.smtp"));
    }

    @Test
    void shouldCreateNLVersion() {
        Assertions.assertDoesNotThrow(() -> NLVersion.of("0.0.0"));
        Assertions.assertDoesNotThrow(() -> NLVersion.of("0.0.0TEST"));
        Assertions.assertDoesNotThrow(() -> NLVersion.of("0.0.0Test"));
        Assertions.assertDoesNotThrow(NLVersion::new);
        Assertions.assertDoesNotThrow(NLVersion::new);
        Assertions.assertDoesNotThrow(NLVersion::new);
    }

    @Test
    void shouldNotCreateNLVersion_ThrowRegexNotMatchException() {
        Assertions.assertThrows(RegexNotMatchException.class, () -> NLVersion.of("a.b.c"));
        Assertions.assertThrows(RegexNotMatchException.class, () -> NLVersion.of("0.0.0-"));
        Assertions.assertThrows(RegexNotMatchException.class, () -> NLVersion.of("000"));
        Assertions.assertThrows(RegexNotMatchException.class, () -> NLVersion.of("ee0"));
        Assertions.assertThrows(RegexNotMatchException.class, () -> NLVersion.of("1.1-test"));
        Assertions.assertThrows(RegexNotMatchException.class, () -> NLVersion.of("1.1test"));
    }

    //* ---------- NLVersion ---------- *//
    @Test
    void shouldCreatePackagePrivateNLVersion(){
        Assertions.assertDoesNotThrow(NLVersion::new);
        Assertions.assertDoesNotThrow(NLVersion::new);
        Assertions.assertDoesNotThrow(NLVersion::new);
    }

    @Test
    void shouldCompareNLVersion() {
        String smtp = "0.0.0";

        Assertions.assertTrue(NLVersion.of(smtp).canEqual(NLVersion.of(smtp)));
        Assertions.assertFalse(NLVersion.of(smtp).canEqual(smtp));
        Assertions.assertEquals(NLVersion.of(smtp), NLVersion.of(smtp));
        Assertions.assertEquals(NLVersion.of(smtp).toString(), NLVersion.of(smtp).toString());
        Assertions.assertEquals(NLVersion.of(smtp).hashCode(), NLVersion.of(smtp).hashCode());
        Assertions.assertNotEquals(NLVersion.of("0.0.0TEST"), NLVersion.of("0.0.0SNAP"));
    }
}
