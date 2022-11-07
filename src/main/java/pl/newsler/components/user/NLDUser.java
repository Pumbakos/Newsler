package pl.newsler.components.user;

import lombok.Builder;
import lombok.Value;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLType;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;

@Value
@Builder
public class NLDUser {
    NLId id;
    NLEmail email;
    NLName name;
    NLLastName lastName;
    NLPassword password;
    NLSmtpAccount smtpAccount;
    NLSecretKey secretKey;
    NLAppKey appKey;
    NLType role;
    boolean enabled;
    boolean credentialsExpired;
}