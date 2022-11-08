package pl.newsler.components.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;

public interface IUserService extends UserDetailsService {
    NLDUser getById(NLId id) throws UserDataNotFineException;

    NLId create(NLFirstName name, NLLastName lastName, NLEmail email, NLPassword password) throws UserDataNotFineException;

    void update(NLId id, NLAppKey appKey, NLSecretKey secretKey, NLSmtpAccount smtpAccount) throws UserDataNotFineException;

    void delete(NLId id, NLPassword password) throws UserDataNotFineException;

    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
