package pl.newsler.components.user.models;

import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;

public record UserUpdateRequest(NLId id, NLAppKey appKey, NLSecretKey secretKey, NLSmtpAccount smtpAccount) {
}
