package pl.newsler.components.user;

import lombok.Builder;
import lombok.Value;
import pl.newsler.commons.models.Email;
import pl.newsler.commons.models.LastName;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLRole;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.commons.models.Name;
import pl.newsler.commons.models.Password;

@Value
@Builder
public class NLDUser {
    NLId id;
    Email email;
    Name firstName;
    LastName lastName;
    Password password;
    NLSmtpAccount smtpAccount;
    NLSecretKey secretKey;
    NLAppKey appKey;
    NLRole role;
    boolean enabled;
    boolean credentialsExpired;
}
