package pl.newsler.components.user;

import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;

public interface IUserCrudService {
    NLDUser getById(NLId id) throws UserDataNotFineException;

    NLId create(NLFirstName name, NLLastName lastName, NLEmail email, NLPassword password) throws UserDataNotFineException;

    void update(NLId id, NLAppKey appKey, NLSecretKey secretKey, NLSmtpAccount smtpAccount) throws UserDataNotFineException;

    void delete(NLId id, NLPassword password) throws UserDataNotFineException;
}