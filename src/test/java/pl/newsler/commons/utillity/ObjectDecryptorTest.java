package pl.newsler.commons.utillity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.newsler.commons.exception.DecryptionException;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.components.signup.usecase.UserCreateRequest;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.components.user.usecase.UserDeleteRequest;
import pl.newsler.components.user.usecase.UserGetRequest;
import pl.newsler.components.user.usecase.UserUpdateRequest;
import pl.newsler.security.NLIPasswordEncoder;
import pl.newsler.security.StubNLPasswordEncoder;
import pl.newsler.testcommons.ObjectEncoder;

import java.util.UUID;

import static pl.newsler.testcommons.TestUserUtils.email;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.lastName;
import static pl.newsler.testcommons.TestUserUtils.secretOrAppKey;
import static pl.newsler.testcommons.TestUserUtils.smtpAccount;

class ObjectDecryptorTest {
    private final NLIPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final ObjectDecryptor decryptor = new ObjectDecryptor(passwordEncoder);
    private final ObjectEncoder encoder = new ObjectEncoder(passwordEncoder);
    private final TestUserFactory factory = new TestUserFactory();

    @Test
    void shouldDecryptUserGetRequestWhenEncryptedProperly() {
        UserGetRequest request = encoder.encrypt(new UserGetRequest(factory.dashed().getEmail().getValue(), factory.dashed_plainPassword()));
        Assertions.assertDoesNotThrow(() -> decryptor.decrypt(request));
    }

    @Test
    void shouldNotDecryptUserGetRequestWhenEncryptedImproperly() {
        UserGetRequest emptyValuesRequest = new UserGetRequest("", "");
        UserGetRequest blankValuesRequest = new UserGetRequest("  ", "  ");
        UserGetRequest nullValuesRequest = new UserGetRequest(null, null);
        UserGetRequest invalidEncryptedValuesRequest = new UserGetRequest(email(), "Pa$$woer*@!#");
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt((UserGetRequest) null));
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt(emptyValuesRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt(blankValuesRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt(nullValuesRequest));
        Assertions.assertThrows(DecryptionException.class, () -> decryptor.decrypt(invalidEncryptedValuesRequest));
    }

    @Test
    void shouldDecryptUserCreateRequestWhenEncryptedProperly() {
        UserCreateRequest request = encoder.encrypt(new UserCreateRequest(firstName(), lastName(), email(), "UJk6ds81#@^dsa"));
        Assertions.assertDoesNotThrow(() -> decryptor.decrypt(request));
    }

    @Test
    void shouldNotDecryptUserCreateRequestWhenEncryptedImproperly() {
        UserCreateRequest emptyValuesRequest = new UserCreateRequest("", "", "", "");
        UserCreateRequest blankValuesRequest = new UserCreateRequest("  ", "  ", "  ", "  ");
        UserCreateRequest nullValuesRequest = new UserCreateRequest(null, null, null, null);
        UserCreateRequest invalidEncryptedValuesRequest = new UserCreateRequest(firstName(), lastName(), email(), "Pa$$woer*@!#");
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt((UserCreateRequest) null));
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt(emptyValuesRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt(blankValuesRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt(nullValuesRequest));
        Assertions.assertThrows(DecryptionException.class, () -> decryptor.decrypt(invalidEncryptedValuesRequest));
    }

    @Test
    void shouldDecryptUserUpdateRequestWhenEncryptedProperly() {
        UserUpdateRequest request = encoder.encrypt(new UserUpdateRequest(email(), secretOrAppKey(), secretOrAppKey(), smtpAccount()));
        Assertions.assertDoesNotThrow(() -> decryptor.decrypt(request));
    }

    @Test
    void shouldNotDecryptUserUpdateRequestWhenEncryptedImproperly() {
        UserUpdateRequest emptyValuesRequest = new UserUpdateRequest("", "", "", "");
        UserUpdateRequest blankValuesRequest = new UserUpdateRequest("  ", "  ", "  ", "  ");
        UserUpdateRequest nullValuesRequest = new UserUpdateRequest(null, null, null, null);
        UserUpdateRequest invalidEncryptedValuesRequest = new UserUpdateRequest(email(), secretOrAppKey(), secretOrAppKey(), smtpAccount());
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt((UserCreateRequest) null));
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt(emptyValuesRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt(blankValuesRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt(nullValuesRequest));
        Assertions.assertThrows(DecryptionException.class, () -> decryptor.decrypt(invalidEncryptedValuesRequest));
    }

    @Test
    void shouldDecryptUserDeleteRequestWhenEncryptedProperly() {
        UserDeleteRequest request = encoder.encrypt(new UserDeleteRequest(UUID.randomUUID().toString(), "Pa$$woer*@!#"));
        Assertions.assertDoesNotThrow(() -> decryptor.decrypt(request));
    }

    @Test
    void shouldNotDecryptUserDeleteRequestWhenEncryptedImproperly() {
        UserDeleteRequest emptyValuesRequest = new UserDeleteRequest("", "");
        UserDeleteRequest blankValuesRequest = new UserDeleteRequest("  ", "  ");
        UserDeleteRequest nullValuesRequest = new UserDeleteRequest(null, null);
        UserDeleteRequest invalidEncryptedValuesRequest = new UserDeleteRequest(UUID.randomUUID().toString(), "Pa$$woer*@!#");
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt((UserCreateRequest) null));
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt(emptyValuesRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt(blankValuesRequest));
        Assertions.assertThrows(InvalidUserDataException.class, () -> decryptor.decrypt(nullValuesRequest));
        Assertions.assertThrows(DecryptionException.class, () -> decryptor.decrypt(invalidEncryptedValuesRequest));
    }
}