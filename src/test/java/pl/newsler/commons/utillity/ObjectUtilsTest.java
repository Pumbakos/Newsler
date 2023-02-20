package pl.newsler.commons.utillity;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.newsler.components.signup.usecase.UserCreateRequest;
import pl.newsler.components.signup.usecase.UserResendTokenRequest;
import pl.newsler.components.user.usecase.UserDeleteRequest;
import pl.newsler.components.user.usecase.UserGetRequest;
import pl.newsler.components.user.usecase.UserUpdateRequest;

import java.util.UUID;

import static pl.newsler.testcommons.TestUserUtils.email;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.lastName;
import static pl.newsler.testcommons.TestUserUtils.secretOrAppKey;
import static pl.newsler.testcommons.TestUserUtils.smtpAccount;

class ObjectUtilsTest {
    @Test
    void shouldReturnTrueWhenUserCreateRequestOrItsValuesAreBlank() {
        Assertions.assertTrue(ObjectUtils.isBlank((UserCreateRequest) null));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserCreateRequest(null, null, null, null)));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserCreateRequest("", "", "", "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserCreateRequest(" ", " ", " ", " ")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserCreateRequest(firstName(), "", "", "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserCreateRequest("", lastName(), " ", " ")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserCreateRequest("", "", email(), "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserCreateRequest("", "", "", "HB[-x_[$gaCFuNdn")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserCreateRequest(firstName(), lastName(), email(), null)));
    }

    @Test
    void shouldReturnFalseWhenUserCreateRequestOrItsValuesAreBlank() {
        Assertions.assertFalse(ObjectUtils.isNotBlank((UserCreateRequest) null));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserCreateRequest(null, null, null, null)));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserCreateRequest("", "", "", "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserCreateRequest(" ", " ", " ", " ")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserCreateRequest(firstName(), "", "", "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserCreateRequest("", lastName(), " ", " ")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserCreateRequest("", "", email(), "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserCreateRequest("", "", "", "HB[-x_[$gaCFuNdn")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserCreateRequest(firstName(), lastName(), email(), null)));
    }

    @Test
    void shouldReturnFalseWhenUserCreateRequestOrItsValuesAreNotBlank() {
        Assertions.assertFalse(ObjectUtils.isBlank(new UserCreateRequest(firstName(), lastName(), email(), "HB[-x_[$gaCFuNdn")));
    }

    @Test
    void shouldReturnTrueWhenUserCreateRequestOrItsValuesAreNotBlank() {
        Assertions.assertTrue(ObjectUtils.isNotBlank(new UserCreateRequest(firstName(), lastName(), email(), "HB[-x_[$gaCFuNdn")));
    }

    @Test
    void shouldReturnTrueWhenUserGetRequestOrItsValuesAreBlank() {
        Assertions.assertTrue(ObjectUtils.isBlank((UserGetRequest) null));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserGetRequest(null, null)));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserGetRequest("", "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserGetRequest(" ", " ")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserGetRequest(email(), "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserGetRequest("", "b6W[Q]aY6_d>3d[L")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserGetRequest("", "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserGetRequest("", "")));
    }

    @Test
    void shouldReturnFalseWhenUserGetRequestOrItsValuesAreNotBlank() {
        Assertions.assertFalse(ObjectUtils.isNotBlank((UserGetRequest) null));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserGetRequest(null, null)));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserGetRequest("", "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserGetRequest(" ", " ")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserGetRequest(email(), "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserGetRequest("", "b6W[Q]aY6_d>3d[L")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserGetRequest("", "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserGetRequest("", "")));
    }

    @Test
    void shouldReturnFalseWhenUserGetRequestOrItsValuesAreBlank() {
        Assertions.assertFalse(ObjectUtils.isBlank(new UserGetRequest(email(), "HB[-x_[$gaCFuNdn")));
    }

    @Test
    void shouldReturnTrueWhenUserGetRequestOrItsValuesAreNotBlank() {
        Assertions.assertTrue(ObjectUtils.isNotBlank(new UserGetRequest(email(), "HB[-x_[$gaCFuNdn")));
    }

    @Test
    void shouldReturnTrueWhenUserUpdateRequestOrItsValuesAreBlank() {
        Assertions.assertTrue(ObjectUtils.isBlank((UserUpdateRequest) null));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserUpdateRequest(null, null, null, null)));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserUpdateRequest("", "", "", "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserUpdateRequest(" ", " ", " ", " ")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserUpdateRequest(email(), "", "", "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserUpdateRequest("", secretOrAppKey(), " ", " ")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserUpdateRequest("", "", secretOrAppKey(), "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserUpdateRequest("", "", "", smtpAccount())));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserUpdateRequest(email(), secretOrAppKey(), secretOrAppKey(), null)));
    }

    @Test
    void shouldReturnFalseWhenUserUpdateRequestOrItsValuesAreNotBlank() {
        Assertions.assertFalse(ObjectUtils.isNotBlank((UserUpdateRequest) null));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserUpdateRequest(null, null, null, null)));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserUpdateRequest("", "", "", "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserUpdateRequest(" ", " ", " ", " ")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserUpdateRequest(email(), "", "", "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserUpdateRequest("", secretOrAppKey(), " ", " ")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserUpdateRequest("", "", secretOrAppKey(), "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserUpdateRequest("", "", "", smtpAccount())));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserUpdateRequest(email(), secretOrAppKey(), secretOrAppKey(), null)));
    }

    @Test
    void shouldReturnFalseWhenUserUpdateRequestOrItsValuesAreBlank() {
        Assertions.assertFalse(ObjectUtils.isBlank(new UserUpdateRequest(email(), secretOrAppKey(), secretOrAppKey(), smtpAccount())));
    }

    @Test
    void shouldReturnTrueWhenUserUpdateRequestOrItsValuesAreNotBlank() {
        Assertions.assertTrue(ObjectUtils.isNotBlank(new UserUpdateRequest(email(), secretOrAppKey(), secretOrAppKey(), smtpAccount())));
    }

    @Test
    void shouldReturnTrueWhenUserDeleteRequestOrItsValuesAreBlank() {
        Assertions.assertTrue(ObjectUtils.isBlank((UserDeleteRequest) null));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserDeleteRequest(null, null)));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserDeleteRequest("", "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserDeleteRequest(" ", " ")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserDeleteRequest(UUID.randomUUID().toString(), "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserDeleteRequest("", "b6W[Q]aY6_d>3d[L")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserDeleteRequest("", "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserDeleteRequest("", "")));
    }

    @Test
    void shouldReturnFalseWhenUserDeleteRequestOrItsValuesAreNotBlank() {
        Assertions.assertFalse(ObjectUtils.isNotBlank((UserDeleteRequest) null));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserDeleteRequest(null, null)));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserDeleteRequest("", "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserDeleteRequest(" ", " ")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserDeleteRequest(UUID.randomUUID().toString(), "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserDeleteRequest("", "b6W[Q]aY6_d>3d[L")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserDeleteRequest("", "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserDeleteRequest("", "")));
    }

    @Test
    void shouldReturnFalseWhenUserDeleteRequestOrItsValuesAreBlank() {
        Assertions.assertFalse(ObjectUtils.isBlank(new UserDeleteRequest(UUID.randomUUID().toString(), "HB[-x_[$gaCFuNdn")));
    }

    @Test
    void shouldReturnTrueWhenUserDeleteRequestOrItsValuesAreNotBlank() {
        Assertions.assertTrue(ObjectUtils.isNotBlank(new UserDeleteRequest(UUID.randomUUID().toString(), "HB[-x_[$gaCFuNdn")));
    }

    @Test
    void shouldReturnTrueWhenUserResendTokenRequestOrItsValuesAreBlank() {
        Assertions.assertTrue(ObjectUtils.isBlank((UserResendTokenRequest) null));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserResendTokenRequest(null, null)));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserResendTokenRequest("", "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserResendTokenRequest(" ", " ")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserResendTokenRequest(email(), "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserResendTokenRequest("", "b6W[Q]aY6_d>3d[L")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserResendTokenRequest("", "")));
        Assertions.assertTrue(ObjectUtils.isBlank(new UserResendTokenRequest("", "")));
    }

    @Test
    void shouldReturnFalseWhenUserResendTokenRequestOrItsValuesAreNotBlank() {
        Assertions.assertFalse(ObjectUtils.isNotBlank((UserResendTokenRequest) null));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserResendTokenRequest(null, null)));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserResendTokenRequest("", "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserResendTokenRequest(" ", " ")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserResendTokenRequest(email(), "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserResendTokenRequest("", "b6W[Q]aY6_d>3d[L")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserResendTokenRequest("", "")));
        Assertions.assertFalse(ObjectUtils.isNotBlank(new UserResendTokenRequest("", "")));
    }

    @Test
    void shouldReturnFalseWhenUserResendTokenRequestOrItsValuesAreBlank() {
        Assertions.assertFalse(ObjectUtils.isBlank(new UserResendTokenRequest(email(), "HB[-x_[$gaCFuNdn")));
    }

    @Test
    void shouldReturnTrueWhenUserResendTokenRequestOrItsValuesAreNotBlank() {
        Assertions.assertTrue(ObjectUtils.isNotBlank(new UserResendTokenRequest(email(), "HB[-x_[$gaCFuNdn")));
    }
}