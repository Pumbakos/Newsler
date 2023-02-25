package pl.newsler.components.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import pl.newsler.commons.model.NLAppKey;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.commons.model.NLUserType;
import pl.newsler.commons.model.NLUuid;

@Value
@Builder(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NLDUser {
    NLUuid id;
    NLEmail email;
    NLFirstName name;
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

    public NLUser toUser() {
        final NLUser user = new NLUser();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(name);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setSmtpAccount(smtpAccount);
        user.setSecretKey(secretKey);
        user.setAppKey(appKey);
        user.setRole(role);
        user.setEnabled(enabled);
        return user;
    }
}