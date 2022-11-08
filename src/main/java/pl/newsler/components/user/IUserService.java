package pl.newsler.components.user;

import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;

public interface IUserService {
    NLDUser getById(NLId id);
    NLId create(NLFirstName name, NLLastName lastName, NLEmail email, NLPassword password);

    boolean update(NLId id, NLAppKey appKey, NLSecretKey secretKey, NLSmtpAccount smtpAccount);

    boolean delete(NLId id, NLPassword password);
}
