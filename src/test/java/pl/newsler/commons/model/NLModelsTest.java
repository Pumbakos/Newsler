package pl.newsler.commons.model;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import pl.newsler.commons.exception.RegexNotMatchException;
import pl.newsler.testcommons.TestUserUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import static pl.newsler.testcommons.TestUserUtils.domain;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.lastName;
import static pl.newsler.testcommons.TestUserUtils.secretOrAppKey;
import static pl.newsler.testcommons.TestUserUtils.username;

@SuppressWarnings({"java:S5778"})
class NLModelsTest {
    private static final Faker faker = new Faker();

    //* ---------- APP KEY & SECRET KEY ---------- *//
    @Test
    void shouldCreatePackagePrivateNLAppKeyAndNLSecretKey() {
        Assertions.assertDoesNotThrow(NLSecretKey::new);
        Assertions.assertDoesNotThrow(NLAppKey::new);
    }

    @Test
    void shouldValidateNLAppKeyAndNLSecretKey() {
        Assertions.assertTrue(NLAppKey.of(TestUserUtils.secretOrAppKey()).validate());
        Assertions.assertTrue(NLSecretKey.of(TestUserUtils.secretOrAppKey()).validate());
    }

    @Test
    void shouldCompareNLAppKeysAndNLSecretKeys() {
        final String first = secretOrAppKey();
        final String second = secretOrAppKey();

        final NLAppKey appKey = NLAppKey.of(first);
        Assertions.assertTrue(appKey.canEqual(appKey));
        Assertions.assertFalse(appKey.canEqual(first));
        Assertions.assertEquals(appKey, NLAppKey.of(first));
        Assertions.assertEquals(appKey.toString(), appKey.toString());
        Assertions.assertEquals(appKey.hashCode(), appKey.hashCode());
        Assertions.assertNotEquals(appKey, NLAppKey.of(second));
        Assertions.assertNotEquals(appKey, null);
        Assertions.assertNotEquals(appKey, "");
        Assertions.assertNotEquals(appKey.toString(), NLAppKey.of(second).toString());
        Assertions.assertNotEquals(appKey.hashCode(), NLAppKey.of(second).hashCode());
        Assertions.assertNotEquals(NLAppKey.of(first), NLAppKey.of(second));

        final NLSecretKey secretKey = NLSecretKey.of(first);
        Assertions.assertTrue(secretKey.canEqual(secretKey));
        Assertions.assertFalse(secretKey.canEqual(first));
        Assertions.assertEquals(secretKey, NLSecretKey.of(first));
        Assertions.assertEquals(secretKey.toString(), secretKey.toString());
        Assertions.assertEquals(secretKey.hashCode(), secretKey.hashCode());
        Assertions.assertNotEquals(secretKey, NLSecretKey.of(second));
        Assertions.assertNotEquals(appKey, null);
        Assertions.assertNotEquals(appKey, "");
        Assertions.assertNotEquals(secretKey.toString(), NLSecretKey.of(second).toString());
        Assertions.assertNotEquals(secretKey.hashCode(), NLSecretKey.of(second).hashCode());
        Assertions.assertNotEquals(NLSecretKey.of(first), NLSecretKey.of(second));
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
        String first = String.format("%s@%s.dev", username(), domain());
        String second = String.format("%s@%s.dev", username(), domain());

        NLEmail email = NLEmail.of(first);
        Assertions.assertTrue(email.canEqual(email));
        Assertions.assertFalse(email.canEqual(first));
        Assertions.assertEquals(email, NLEmail.of(first));
        Assertions.assertEquals(email.toString(), email.toString());
        Assertions.assertEquals(email.hashCode(), email.hashCode());
        Assertions.assertNotEquals(email, NLEmail.of(second));
        Assertions.assertNotEquals(email, null);
        Assertions.assertNotEquals(email, "");
        Assertions.assertNotEquals(email.toString(), NLEmail.of(second).toString());
        Assertions.assertNotEquals(email.hashCode(), NLEmail.of(second).hashCode());
        Assertions.assertNotEquals(NLEmail.of(first), NLEmail.of(second));
    }

    //* ---------- NLEmailMessage ---------- *//
    @Test
    void shouldCreatePackagePrivateNLEmailMessage() {
        Assertions.assertDoesNotThrow(NLEmailMessage::new);
    }

    @Test
    void shouldValidateNLEmailMessage() {
        Assertions.assertTrue(NLEmailMessage.of("VALID").validate());
        Assertions.assertTrue(NLEmailMessage.of(generateMessage(50)).validate());
        Assertions.assertTrue(NLEmailMessage.of(generateMessage(49)).validate());
    }

    @Test
    void shouldNotValidateNLEmailMessage_RegexDoesNotMatchAndBlankValue() {
        Assertions.assertFalse(NLEmailMessage.of("").validate());
        Assertions.assertFalse(NLEmailMessage.of(null).validate());
        Assertions.assertFalse(NLEmailMessage.of(generateMessage(51)).validate());
    }

    @Test
    void shouldCompareNLEmailMessages() {
        final String message = generateMessage(4);
        final NLEmailMessage email = NLEmailMessage.of(message);
        final NLEmailMessage second = NLEmailMessage.of(generateMessage(4));

        Assertions.assertTrue(email.canEqual(email));
        Assertions.assertFalse(email.canEqual(message));
        Assertions.assertEquals(email, NLEmailMessage.of(message));
        Assertions.assertEquals(email.toString(), email.toString());
        Assertions.assertEquals(email.hashCode(), email.hashCode());
        Assertions.assertNotEquals(email, second);
        Assertions.assertNotEquals(email, null);
        Assertions.assertNotEquals(email, "");
        Assertions.assertNotEquals(email.toString(), second.toString());
        Assertions.assertNotEquals(email.hashCode(), second.hashCode());
        Assertions.assertNotEquals(NLEmailMessage.of(message), second);
    }

    //* ---------- NLExecutionDate ---------- *//
    @Test
    void shouldCreatePackagePrivateNLExecutionDate() {
        Assertions.assertDoesNotThrow(NLExecutionDate::new);
    }

    @Test
    void shouldValidateNLExecutionDate() {
        Assertions.assertTrue(NLExecutionDate.of(LocalDateTime.now()).validate());
        Assertions.assertTrue(NLExecutionDate.of(ZonedDateTime.now()).validate());
    }

    @Test
    void shouldNotValidateNLExecutionDate_RegexDoesNotMatch() {
        Assertions.assertFalse(NLExecutionDate.of((LocalDateTime) null).validate());
        Assertions.assertFalse(NLExecutionDate.of((ZonedDateTime) null).validate());
    }

    @Test
    void shouldCompareNLExecutionDate() {
        final LocalDateTime now = LocalDateTime.now();
        final NLExecutionDate nowCopy = NLExecutionDate.of(now);
        final LocalDateTime then = LocalDateTime.now().plusMinutes(5L);

        Assertions.assertTrue(nowCopy.canEqual(nowCopy));
        Assertions.assertFalse(nowCopy.canEqual(now));
        Assertions.assertEquals(nowCopy, NLExecutionDate.of(now));
        Assertions.assertEquals(nowCopy.toString(), nowCopy.toString());
        Assertions.assertEquals(nowCopy.hashCode(), nowCopy.hashCode());
        Assertions.assertNotEquals(nowCopy, NLExecutionDate.of(then));
        Assertions.assertNotEquals(nowCopy, null);
        Assertions.assertNotEquals(nowCopy, "");
        Assertions.assertNotEquals(nowCopy.toString(), NLExecutionDate.of(then).toString());
        Assertions.assertNotEquals(nowCopy.hashCode(), NLExecutionDate.of(then).hashCode());
        Assertions.assertNotEquals(NLExecutionDate.of(now), NLExecutionDate.of(then));
    }


    //* ---------- NLFirstName ---------- *//
    @Test
    void shouldCreatePackagePrivateNLFirstName() {
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
        String first = "Newsler";
        String second = "Newslertest";

        NLFirstName firstName = NLFirstName.of(first);
        Assertions.assertTrue(firstName.canEqual(firstName));
        Assertions.assertFalse(firstName.canEqual(first));
        Assertions.assertEquals(firstName, NLFirstName.of(first));
        Assertions.assertEquals(firstName.toString(), firstName.toString());
        Assertions.assertEquals(firstName.hashCode(), firstName.hashCode());
        Assertions.assertNotEquals(firstName, NLFirstName.of(second));
        Assertions.assertNotEquals(firstName, null);
        Assertions.assertNotEquals(firstName, "");
        Assertions.assertNotEquals(firstName.toString(), NLFirstName.of(second).toString());
        Assertions.assertNotEquals(firstName.hashCode(), NLFirstName.of(second).hashCode());
        Assertions.assertNotEquals(NLFirstName.of(first), NLFirstName.of(second));
    }

    //* ---------- NLId ---------- *//
    @Test
    void shouldCreatePackagePrivateNLId() {
        Assertions.assertDoesNotThrow(NLId::new);
    }

    @Test
    void shouldValidateNLId() {
        Assertions.assertTrue(NLId.of("1").validate());
        Assertions.assertTrue(NLId.of(1L).validate());
    }

    @Test
    void shouldNotValidateNLId() {
        Assertions.assertFalse(NLId.of("1L").validate());
        Assertions.assertFalse(NLId.of("3P").validate());
        Assertions.assertFalse(NLId.of(-1L).validate());
        Assertions.assertFalse(NLId.of(0).validate());
        Assertions.assertFalse(NLId.of("-1L").validate());
        Assertions.assertFalse(NLId.of("0").validate());
    }

    @Test
    void shouldCompareNLId() {
        final long first = 1L;
        final long second = 2L;
        final NLId id = NLId.of(first);

        Assertions.assertTrue(id.canEqual(id));
        Assertions.assertFalse(id.canEqual(first));
        Assertions.assertEquals(id, NLId.of(first));
        Assertions.assertEquals(id.toString(), id.toString());
        Assertions.assertEquals(id.toString(), NLId.of(first).toString());
        Assertions.assertEquals(id.hashCode(), id.hashCode());
        Assertions.assertEquals(id.hashCode(), NLId.of(first).hashCode());
        Assertions.assertNotEquals(id, second);
        Assertions.assertNotEquals(id, null);
        Assertions.assertNotEquals(id, "");
        Assertions.assertNotEquals(NLId.of(first), NLId.of(second));
    }

    //* ---------- NLLastName ---------- *//
    @Test
    void shouldCreatePackagePrivateNLLastName() {
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
        String first = "Newsler";
        String second = "Newslertest";

        NLLastName lastName = NLLastName.of(first);
        Assertions.assertTrue(lastName.canEqual(lastName));
        Assertions.assertFalse(lastName.canEqual(first));
        Assertions.assertEquals(lastName, NLLastName.of(first));
        Assertions.assertEquals(lastName.toString(), lastName.toString());
        Assertions.assertEquals(lastName.hashCode(), lastName.hashCode());
        Assertions.assertNotEquals(lastName, NLLastName.of(second));
        Assertions.assertNotEquals(lastName, null);
        Assertions.assertNotEquals(lastName, "");
        Assertions.assertNotEquals(lastName.toString(), NLLastName.of(second).toString());
        Assertions.assertNotEquals(lastName.hashCode(), NLLastName.of(second).hashCode());
        Assertions.assertNotEquals(NLLastName.of(first), NLLastName.of(second));
    }

    //* ---------- NLNickname ---------- *//
    @Test
    void shouldCreatePackagePrivateNLNickname() {
        Assertions.assertDoesNotThrow(NLNickname::new);
    }

    @Test
    void shouldValidateNLNickname() {
        Assertions.assertTrue(NLNickname.of(TestUserUtils.firstName()).validate());
        Assertions.assertTrue(NLNickname.of(TestUserUtils.firstName()).validate());
    }

    @Test
    void shouldNotValidateNLNickname_RegexDoesNotMatch() {
        Assertions.assertFalse(NLNickname.of(null).validate());
        Assertions.assertFalse(NLNickname.of("").validate());
        Assertions.assertFalse(NLNickname.of("1").validate());
        Assertions.assertFalse(NLNickname.of("nurserysplendidsplitagooffernursery").validate());
    }

    @Test
    void shouldCompareNLNickname() {
        final String username = firstName();
        final NLNickname nickname = NLNickname.of(username);
        final NLNickname copy = NLNickname.of(username);

        Assertions.assertTrue(nickname.canEqual(nickname));
        Assertions.assertFalse(nickname.canEqual(username));
        Assertions.assertEquals(nickname, copy);
        Assertions.assertEquals(nickname.toString(), nickname.toString());
        Assertions.assertEquals(nickname.hashCode(), nickname.hashCode());
        Assertions.assertNotEquals(nickname, NLNickname.of(firstName()));
        Assertions.assertNotEquals(nickname, null);
        Assertions.assertNotEquals(nickname, "");
        Assertions.assertNotEquals(nickname.toString(), NLNickname.of(firstName()).toString());
        Assertions.assertNotEquals(nickname.hashCode(), NLNickname.of(username()).hashCode());
        Assertions.assertNotEquals(NLNickname.of(firstName()), NLNickname.of(firstName()));
    }

    //* ---------- NLPassword ---------- *//
    @Test
    void shouldCreatePackagePrivateNLPassword() {
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
        final String first = "U$Ad3na923mas$dmi";
        final String second = "Pa$$word7hat^match3$";
        final NLPassword password = NLPassword.of(first);

        Assertions.assertTrue(password.canEqual(password));
        Assertions.assertFalse(password.canEqual(first));
        Assertions.assertEquals(password, NLPassword.of(first));
        Assertions.assertEquals(password.toString(), password.toString());
        Assertions.assertEquals(password.hashCode(), password.hashCode());
        Assertions.assertNotEquals(password, NLPassword.of(second));
        Assertions.assertNotEquals(password, null);
        Assertions.assertNotEquals(password, "");
        Assertions.assertNotEquals(password.toString(), NLPassword.of(second).toString());
        Assertions.assertNotEquals(password.hashCode(), NLPassword.of(second).hashCode());
        Assertions.assertNotEquals(password, NLPassword.of(second));
    }

    //* ---------- NLSmtpAccount ---------- *//
    @Test
    void shouldCreatePackagePrivateNLSmtpAccount() {
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
        final String first = "3.test.smtp";
        final String second = "3.model.smtp";
        final NLSmtpAccount smtpAccount = NLSmtpAccount.of(first);

        Assertions.assertTrue(smtpAccount.canEqual(smtpAccount));
        Assertions.assertFalse(smtpAccount.canEqual(first));
        Assertions.assertFalse(smtpAccount.canEqual(first));
        Assertions.assertEquals(smtpAccount, NLSmtpAccount.of(first));
        Assertions.assertEquals(smtpAccount.toString(), smtpAccount.toString());
        Assertions.assertEquals(smtpAccount.hashCode(), smtpAccount.hashCode());
        Assertions.assertNotEquals(smtpAccount, NLSmtpAccount.of(second));
        Assertions.assertNotEquals(smtpAccount, null);
        Assertions.assertNotEquals(smtpAccount, "");
        Assertions.assertNotEquals(smtpAccount.toString(), NLSmtpAccount.of(second).toString());
        Assertions.assertNotEquals(smtpAccount.hashCode(), NLSmtpAccount.of(second).hashCode());
        Assertions.assertNotEquals(smtpAccount, NLSmtpAccount.of(second));
    }

    //* ---------- NLStringValue ---------- *//
    @Test
    void shouldCreatePackagePrivateNLStringValue() {
        Assertions.assertDoesNotThrow(NLEmailMessage::new);
    }

    @Test
    void shouldValidateNLStringValue() {
        Assertions.assertTrue(NLStringValue.of(generateMessage(1)).validate());
        Assertions.assertTrue(NLStringValue.of(generateMessage(4)).validate());
        Assertions.assertTrue(NLStringValue.of(generateMessage(9)).validate());
        Assertions.assertTrue(NLStringValue.of(generateMessage(15)).validate());
        Assertions.assertTrue(NLStringValue.of(generateMessage(51)).validate());
        Assertions.assertTrue(NLStringValue.of(generateMessage(100)).validate());
    }

    @Test
    void shouldCompareNLStringValues() {
        final String message = generateMessage(4);
        final NLStringValue value = NLStringValue.of(message);
        final NLStringValue second = NLStringValue.of(generateMessage(4));

        Assertions.assertTrue(value.canEqual(value));
        Assertions.assertFalse(value.canEqual(message));
        Assertions.assertEquals(value, NLStringValue.of(message));
        Assertions.assertEquals(value.toString(), value.toString());
        Assertions.assertEquals(value.hashCode(), value.hashCode());
        Assertions.assertNotEquals(value, second);
        Assertions.assertNotEquals(value, null);
        Assertions.assertNotEquals(value, "");
        Assertions.assertNotEquals(value.toString(), second.toString());
        Assertions.assertNotEquals(value.hashCode(), second.hashCode());
        Assertions.assertNotEquals(NLStringValue.of(message), second);
    }

    //* ---------- NLSubject ---------- *//
    @Test
    void shouldCreatePackagePrivateNLSubject() {
        Assertions.assertDoesNotThrow(NLSubject::new);
    }

    @Test
    void shouldValidateNLSubject() {
        Assertions.assertTrue(NLSubject.of(generateMessage(1)).validate());
    }

    @Test
    void shouldNotValidateNLSubject_RegexDoesNotMatchAndBlankValue() {
        Assertions.assertFalse(NLSubject.of("").validate());
        Assertions.assertFalse(NLSubject.of(null).validate());
        Assertions.assertFalse(NLSubject.of(generateMessage(2)).validate());
    }

    @Test
    void shouldCompareNLSubjects() {
        final String subject = generateMessage(4);
        final NLSubject nlSubject = NLSubject.of(subject);
        final NLSubject second = NLSubject.of(generateMessage(4));

        Assertions.assertTrue(nlSubject.canEqual(nlSubject));
        Assertions.assertFalse(nlSubject.canEqual(subject));
        Assertions.assertEquals(nlSubject, NLSubject.of(subject));
        Assertions.assertEquals(nlSubject.toString(), nlSubject.toString());
        Assertions.assertEquals(nlSubject.hashCode(), nlSubject.hashCode());
        Assertions.assertNotEquals(nlSubject, second);
        Assertions.assertNotEquals(nlSubject, null);
        Assertions.assertNotEquals(nlSubject, "");
        Assertions.assertNotEquals(nlSubject.toString(), second.toString());
        Assertions.assertNotEquals(nlSubject.hashCode(), second.hashCode());
        Assertions.assertNotEquals(NLSubject.of(subject), second);
    }

    //* ---------- NLToken ---------- *//
    @Test
    void shouldCreatePackagePrivateNLToken() {
        Assertions.assertDoesNotThrow(NLToken::new);
    }

    @Test
    void shouldValidateNLToken() {
        final String value = UUID.randomUUID().toString();
        Assertions.assertTrue(NLToken.of(value).validate());
        Assertions.assertTrue(NLToken.of(String.format("%s-%s", value, value)).validate());
    }

    @Test
    void shouldNotValidateNLToken() {
        Assertions.assertFalse(NLToken.of(generateMessage(1)).validate());
        Assertions.assertFalse(NLToken.of(generateMessage(4)).validate());
        Assertions.assertFalse(NLToken.of(generateMessage(9)).validate());
    }

    @Test
    void shouldCompareNLTokens() {
        final String message = UUID.randomUUID().toString();
        final NLToken value = NLToken.of(message);
        final NLToken second = NLToken.of(UUID.randomUUID().toString());

        Assertions.assertTrue(value.canEqual(value));
        Assertions.assertFalse(value.canEqual(message));
        Assertions.assertEquals(value, NLToken.of(message));
        Assertions.assertEquals(value.toString(), value.toString());
        Assertions.assertEquals(value.hashCode(), value.hashCode());
        Assertions.assertNotEquals(value, second);
        Assertions.assertNotEquals(value, null);
        Assertions.assertNotEquals(value, "");
        Assertions.assertNotEquals(value.toString(), second.toString());
        Assertions.assertNotEquals(value.hashCode(), second.hashCode());
        Assertions.assertNotEquals(NLToken.of(message), second);
    }

    //* ---------- NLUuid ---------- *//
    @Test
    void shouldCreatePackagePrivateNLUuid() {
        Assertions.assertDoesNotThrow(NLUuid::new);
    }

    @Test
    void shouldValidateNLUuid() {
        UUID uuid = UUID.randomUUID();
        NLUuid first = NLUuid.of(uuid);

        Assertions.assertTrue(first.validate());
        Assertions.assertEquals(uuid.toString(), first.getValue().substring(4));
        Assertions.assertTrue(first.getValue().startsWith("usr_"));
    }

    @ParameterizedTest
    @EnumSource(value = NLUserType.class)
    void shouldValidateNLUuidByNLType(NLUserType type) {
        UUID uuid = UUID.randomUUID();
        NLUuid id = NLUuid.of(uuid, type);
        Assertions.assertTrue(id.validate());
        Assertions.assertTrue(id.getValue().startsWith(type.getPrefix()));
        Assertions.assertEquals(uuid.toString(), id.getValue().substring(4));
    }

    @Test
    void shouldNotValidateNLUuid_NullType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NLUuid.of(UUID.randomUUID(), (NLIdType) null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> NLUuid.of(UUID.randomUUID(), (NLUserType) null));
    }

    @Test
    void shouldCompareNLUuid() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();

        NLUuid id = NLUuid.of(first);
        Assertions.assertTrue(id.canEqual(id));
        Assertions.assertFalse(id.canEqual(first));
        Assertions.assertEquals(id, NLUuid.of(first));
        Assertions.assertEquals(id.toString(), id.toString());
        Assertions.assertEquals(id.hashCode(), id.hashCode());
        Assertions.assertNotEquals(id, NLUuid.of(second));
        Assertions.assertNotEquals(id, null);
        Assertions.assertNotEquals(id, "");
        Assertions.assertNotEquals(id.toString(), NLUuid.of(second).toString());
        Assertions.assertNotEquals(id.hashCode(), NLUuid.of(second).hashCode());
        Assertions.assertNotEquals(NLUuid.of(UUID.randomUUID()), NLUuid.of(UUID.randomUUID()));
    }

    @Test
    void shouldThrowIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NLUuid.of(UUID.randomUUID(), NLUserType.valueOf("TEST_USER")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> NLUuid.of(UUID.fromString("")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> NLUuid.of(UUID.fromString("user")));
    }

    //* ---------- NLVersion ---------- *//
    @Test
    void shouldCreatePackagePrivateNLVersion() {
        Assertions.assertDoesNotThrow(NLVersion::new);
    }

    @Test
    void shouldCreateNLVersion() {
        Assertions.assertDoesNotThrow(() -> NLVersion.of("0.0.0"));
        Assertions.assertDoesNotThrow(() -> NLVersion.of("0.0.0TEST"));
        Assertions.assertDoesNotThrow(() -> NLVersion.of("0.0.0Test"));
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

    @Test
    void shouldCompareNLVersion() {
        String first = "0.0.0";
        String second = "0.0.0TEST";
        NLVersion version = NLVersion.of(first);

        Assertions.assertTrue(version.canEqual(version));
        Assertions.assertFalse(version.canEqual(first));
        Assertions.assertFalse(version.canEqual(first));
        Assertions.assertEquals(version, NLVersion.of(first));
        Assertions.assertEquals(version.toString(), version.toString());
        Assertions.assertEquals(version.hashCode(), version.hashCode());
        Assertions.assertNotEquals(version, NLVersion.of(second));
        Assertions.assertNotEquals(version, null);
        Assertions.assertNotEquals(version, "");
    }

    private static String generateMessage(final int rounds) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < rounds; i++) {
            builder.append(faker.regexify("[a-zA-Z]{100}"));
        }

        return builder.toString();
    }
}
