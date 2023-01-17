package pl.newsler.components.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.commons.models.NLUserType;
import pl.newsler.components.user.dto.UserGetRequest;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NLDUser {
    NLUuid id;
    NLEmail email;
    NLName name;
    NLLastName lastName;
    NLPassword password;
    NLSmtpAccount smtpAccount;
    NLSecretKey secretKey;
    NLAppKey appKey;
    NLUserType role;
    boolean enabled;
    boolean credentialsNonExpired;

    public static NLDUser of(NLUser user) {
        return NLDUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getNLPassword())
                .smtpAccount(user.getSmtpAccount())
                .secretKey(user.getSecretKey())
                .appKey(user.getAppKey())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .build();
    }
}