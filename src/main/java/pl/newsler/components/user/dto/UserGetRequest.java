package pl.newsler.components.user.dto;

import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLName;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.commons.models.NLUserType;
import pl.newsler.components.user.NLDUser;
import pl.newsler.components.user.NLUser;

public record UserGetRequest(NLId id, NLEmail email, NLName name, NLLastName lastName, NLSmtpAccount smtpAccount,
                             NLSecretKey secretKey, NLAppKey appKey, NLUserType role) {

    public static UserGetRequest of(NLUser user) {
        return NLDUser.of(user).toUserGetRequest();
    }
}
