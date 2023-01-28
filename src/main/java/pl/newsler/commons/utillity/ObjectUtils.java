package pl.newsler.commons.utillity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.signup.dto.UserResendTokenRequest;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserGetRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObjectUtils {
    public static boolean isNotBlank(UserCreateRequest request) {
        return !isBlank(request);
    }

    public static boolean isBlank(UserCreateRequest request) {
        return ((request == null) || (isValueBlank(request.name()) || isValueBlank(request.lastName()) || isValueBlank(request.email()) || isValueBlank(request.password())));
    }

    public static boolean isNotBlank(UserGetRequest request) {
        return !isBlank(request);
    }

    public static boolean isBlank(UserGetRequest request) {
        return ((request == null) || (isValueBlank(request.password()) || isValueBlank(request.email())));
    }

    public static boolean isNotBlank(UserUpdateRequest request) {
        return !isBlank(request);
    }

    public static boolean isBlank(UserUpdateRequest request) {
        return ((request == null) || (isValueBlank(request.appKey()) || isValueBlank(request.secretKey()) || isValueBlank(request.email())) || (isValueBlank(request.smtpAccount())));
    }

    public static boolean isNotBlank(UserDeleteRequest request) {
        return !isBlank(request);
    }

    public static boolean isBlank(UserDeleteRequest request) {
        return ((request == null) || (isValueBlank(request.id()) || isValueBlank(request.password())));
    }

    public static boolean isNotBlank(UserResendTokenRequest request) {
        return !isBlank(request);
    }

    public static boolean isBlank(UserResendTokenRequest request) {
        return ((request == null) || (isValueBlank(request.email()) || isValueBlank(request.password())));
    }



    private static boolean isValueBlank(String value) {
        return StringUtils.isBlank(value);
    }
}
