package pl.newsler.components.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLModel;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.security.NLIPasswordEncoder;

import java.util.Optional;

@RequiredArgsConstructor
class UserService implements UserDetailsService, IUserService {
    private final UserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;
    private final BCryptPasswordEncoder bCrypt;

    @Override
    public NLDUser getById(NLId id) {
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
            throw new UserDataNotFineException();
        }

        NLUser user = new NLUser();
        user.setFirstName(name);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        return userRepository.save(user).getId();
    }

    @Override
    public boolean update(NLId id, NLAppKey appKey, NLSecretKey secretKey, NLSmtpAccount smtpAccount) {
        if (!isDataOk(appKey, secretKey, smtpAccount)) {
            throw new UserDataNotFineException();
        }

        Optional<NLUser> optionalNLUser = userRepository.findById(id);
        if (optionalNLUser.isEmpty()) {
            throw new UserDataNotFineException();
        }

        NLUser nlUser = optionalNLUser.get();
        nlUser.setAppKey(appKey);
        nlUser.setSecretKey(secretKey);
        nlUser.setSmtpAccount(smtpAccount);
        userRepository.save(nlUser);
        return true;
    }

    public boolean delete(NLId id, NLPassword password) {
        if (isPasswordOk(password)) {
            throw new UserDataNotFineException();
        }

        userRepository.findById(id)
                .ifPresentOrElse(
                        user -> bCrypt.matches(password.getValue(), user.getPassword()),
                        () -> {
                            throw new UserDataNotFineException();
                        }
                );
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        NLEmail nlEmail = NLEmail.of(email);
        if (!(nlEmail.validate())) {
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
}
