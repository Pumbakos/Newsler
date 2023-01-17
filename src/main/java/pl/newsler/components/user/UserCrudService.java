package pl.newsler.components.user;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import pl.newsler.api.exceptions.UserDataNotFineException;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLModel;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.commons.models.NLUserType;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.components.user.dto.GetUserRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;
import pl.newsler.security.NLIPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
class UserCrudService implements IUserCrudService {
    private final IUserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;

    @Override
    public @NotNull NLDUser get(final GetUserRequest request) {
        NLEmail email = NLEmail.of(passwordEncoder.decrypt(request.email()));
        NLPassword password = NLPassword.of(passwordEncoder.decrypt(request.password()));

        Optional<NLUser> optionalNLUser = userRepository.findByEmail(email);
        if (optionalNLUser.isEmpty()) {
            throw new UserDataNotFineException("Either email or password is incorrect");
        }
        NLUser user = optionalNLUser.get();

        if (!passwordEncoder.bCrypt().matches(password.getValue(), user.getPassword())) {
            throw new UserDataNotFineException("Either email or password is incorrect");
        }
        return user.map();
    }

    @Override
    public @NotNull NLUuid create(final NLFirstName name, final NLLastName lastName, final NLEmail email, final NLPassword password) {
        if (!isPasswordOk(password)) {
            throw new UserDataNotFineException();
        }

        if (!isDataOk(name, lastName, email)) {
            throw new UserDataNotFineException(String.format("Either name: %s, lastName: %s or email: %s are not valid.", name.getValue(), lastName.getValue(), email.getValue()));
        }

        NLUser user = new NLUser();
        user.setFirstName(name);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(password.getValue())));
        user.setRole(NLUserType.USER);
        user.setId(NLUuid.of(UUID.randomUUID()));
        user.setVersion(UserRepository.version);
        user.setEnabled(false);
        return userRepository.save(user).getId();
    }

    @Override
    public void update(final UserUpdateRequest request) {
        NLUuid uuid = NLUuid.of(passwordEncoder.decrypt(request.id()));
        NLAppKey appKey = NLAppKey.of(passwordEncoder.decrypt(request.appKey()));
        NLSecretKey secretKey = NLSecretKey.of(passwordEncoder.decrypt(request.secretKey()));
        NLSmtpAccount smtpAccount = NLSmtpAccount.of(passwordEncoder.decrypt(request.smtpAccount()));

        if (!uuid.validate()) {
            throw new UserDataNotFineException("ID", "Invalid");
        }

        if (!isDataOk(appKey, secretKey, smtpAccount)) {
            throw new UserDataNotFineException(String.format("Either appKey: %s, secretKey: %s or smtpAccount: %s are not valid.", appKey.getValue(), secretKey.getValue(), smtpAccount.getValue()));
        }

        Optional<NLUser> optionalNLUser = userRepository.findById(uuid);
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
    public void delete(final NLUuid id, final NLPassword password) {
        if (id == null || !id.validate()) {
            throw new UserDataNotFineException("ID", "Invalid");
        }

        if (!isPasswordOk(password)) {
            throw new UserDataNotFineException();
        }

        final Optional<NLUser> optionalNLUser = userRepository.findById(id);
        if ((optionalNLUser.isEmpty())) {
            throw new UserDataNotFineException();
        }

        final NLUser user = optionalNLUser.get();
        if (!passwordEncoder.bCrypt().matches(password.getValue(), user.getPassword())) {
            throw new UserDataNotFineException();
        }

        userRepository.deleteById(user.getId());
    }

    private boolean isDataOk(final NLModel first, final NLModel second, final NLModel third) {
        if (first == null || second == null || third == null) {
            return false;
        }

        return first.validate() && second.validate() && third.validate();
    }

    private boolean isPasswordOk(final NLPassword password) {
        return password != null && password.validate();
    }

    private @NotNull String hash(@NotNull final String password) {
        return passwordEncoder.encrypt(password);
    }
}
