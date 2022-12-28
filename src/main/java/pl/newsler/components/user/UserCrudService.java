package pl.newsler.components.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLModel;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.commons.models.NLUserType;
import pl.newsler.security.NLIPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
class UserCrudService implements IUserCrudService {
    private final UserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;

    @Override
    public NLDUser getById(NLId id) {
        if (id == null) {
            throw new UserDataNotFineException();
        }

        Optional<NLUser> optionalNLUser = userRepository.findById(id);
        if (optionalNLUser.isEmpty()) {
            throw new UserDataNotFineException();
        }
        return optionalNLUser.get().map();
    }

    @Override
    public NLId create(NLFirstName name, NLLastName lastName, NLEmail email, NLPassword password) {
        if (!isPasswordOk(password)) {
            throw new UserDataNotFineException();
        }

        if (!isDataOk(name, lastName, email)) {
            throw new UserDataNotFineException(String.format("name: %s, lastName: %s, email: %s, ", name, lastName, email));
        }

        NLUser user = new NLUser();
        user.setFirstName(name);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(NLPassword.of(hash(password.getValue())));
        user.setRole(NLUserType.USER);
        user.setId(NLId.of(UUID.randomUUID()));
        user.setVersion(UserRepository.version);
        user.setEnabled(true);
        return userRepository.save(user).getId();
    }

    @Override
    public void update(NLId id, NLAppKey appKey, NLSecretKey secretKey, NLSmtpAccount smtpAccount) {
        if (!isDataOk(appKey, secretKey, smtpAccount)) {
            throw new UserDataNotFineException();
        }

        Optional<NLUser> optionalNLUser = userRepository.findById(id);
        if (optionalNLUser.isEmpty()) {
            throw new UserDataNotFineException();
        }

        NLUser nlUser = optionalNLUser.get();
        nlUser.setAppKey(NLAppKey.of(hash(appKey.getValue())));
        nlUser.setSecretKey(NLSecretKey.of(hash(secretKey.getValue())));
        nlUser.setSmtpAccount(NLSmtpAccount.of(hash(smtpAccount.getValue())));
        userRepository.save(nlUser);
    }

    @Override
    public void delete(NLId id, NLPassword password) {
        if (!isPasswordOk(password)) {
            throw new UserDataNotFineException();
        }

        final Optional<NLUser> optionalNLUser = userRepository.findById(id);
        if ((optionalNLUser.isEmpty())) {
            throw new UserDataNotFineException();
        }

        final NLUser user = optionalNLUser.get();
        final String encodedPassword = passwordEncoder.decrypt(user.getPassword());
        if (!password.getValue().equals(encodedPassword)) {
            throw new UserDataNotFineException();
        }

        userRepository.deleteById(user.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        NLEmail nlEmail = NLEmail.of(email);
        if (!nlEmail.validate()) {
            throw new UserDataNotFineException();
        }

        Optional<NLUser> optionalNLUser = userRepository.findByEmail(nlEmail);
        if (optionalNLUser.isEmpty()) {
            throw new UserDataNotFineException();
        }

        return optionalNLUser.get();
    }

    private boolean isDataOk(NLModel first, NLModel second, NLModel third) {
        return first.validate() && second.validate() && third.validate();
    }

    private boolean isPasswordOk(NLPassword password) {
        return password.validate();
    }

    private String hash(String password) {
        return passwordEncoder.encrypt(password);
    }
}
