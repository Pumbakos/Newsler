package pl.newsler.commons.utillity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.newsler.components.emaillabs.usecase.ELAInstantMailRequest;
import pl.newsler.components.emaillabs.usecase.ELAScheduleMailRequest;
import pl.newsler.components.receiver.usecase.ReceiverCreateRequest;
import pl.newsler.components.signup.usecase.UserCreateRequest;
import pl.newsler.components.signup.usecase.UserResendTokenRequest;
import pl.newsler.components.user.usecase.UserDeleteRequest;
import pl.newsler.components.user.usecase.UserGetRequest;
import pl.newsler.components.user.usecase.UserUpdateRequest;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObjectUtils {
    public static boolean isNotBlank(UserCreateRequest request) {
        return !isBlank(request);
    }

    public static boolean isBlank(UserCreateRequest request) {
        return ((request == null) || (isValueBlank(request.name()) || isValueBlank(request.lastName()) ||
                isValueBlank(request.email()) || isValueBlank(request.password())));
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
        return ((request == null) || (isValueBlank(request.appKey()) || isValueBlank(request.secretKey()) ||
                isValueBlank(request.email())) || (isValueBlank(request.smtpAccount())));
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

    public static boolean isBlank(ReceiverCreateRequest request) {
        return ((request == null) || (isValueBlank(request.userUuid()) || isValueBlank(request.email()) || isValueBlank(request.nickname())
                || isValueBlank(request.firstName()) || isValueBlank(request.lastName())));
    }

    public static boolean isBlank(final ELAInstantMailRequest request) {
        return ((request == null) || (isValueBlank(request.from()) || areValuesBlank(request.to()) ||
                isValueBlank(request.subject())) || (isValueBlank(request.message())));
    }

    public static boolean isBlank(final ELAScheduleMailRequest request) {
        return ((request == null) || (isValueBlank(request.from()) || areValuesBlank(request.to()) ||
                isValueBlank(request.subject())) || (isValueBlank(request.message())) ||
                request.timestamp() <= 0 || (isValueBlank(request.zone())));
    }

    private static boolean isValueBlank(String value) {
        return StringUtils.isBlank(value);
    }

    private static boolean areValuesBlank(List<String> values) {
        return StringUtils.isAllBlank(values.toArray(new String[0]));
    }
}
