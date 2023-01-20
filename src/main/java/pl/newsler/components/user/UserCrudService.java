package pl.newsler.components.user;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.exception.UserAlreadyExistsException;
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
import pl.newsler.commons.utillity.ObjectUtils;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserGetRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;
import pl.newsler.security.NLIPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
class UserCrudService implements IUserCrudService {
    private final IUserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;

    @Override
    public @NotNull NLDUser get(final UserGetRequest request) {
        if (ObjectUtils.isBlank(request)) {
            throw new InvalidUserDataException("Data", "Invalid email or password");
        }

        final NLEmail email = NLEmail.of(request.email());
        final NLPassword password = NLPassword.of(request.password());

        if (!email.validate() || !password.validate()) {
            throw new InvalidUserDataException("Data", "Invalid email or password");
        }

        final Optional<NLUser> optionalNLUser = userRepository.findByEmail(email);
        if (optionalNLUser.isEmpty()) {
            throw new InvalidUserDataException("Either email or password is incorrect");
        }

        final NLUser user = optionalNLUser.get();
        if (!passwordEncoder.bCrypt().matches(password.getValue(), user.getPassword())) {
            throw new InvalidUserDataException("Either email or password is incorrect");
        }

        return user.map();
    }

    @Override
    public @NotNull NLUuid create(final NLFirstName name, final NLLastName lastName, final NLEmail email, final NLPassword password) throws UserAlreadyExistsException {
        if (!password.validate()) {
            throw new InvalidUserDataException();
        }

        if (!isDataOk(name, lastName, email)) {
            throw new InvalidUserDataException(String.format("Either name: %s, lastName: %s or email: %s are not valid.", name.getValue(), lastName.getValue(), email.getValue()));
        }

        userRepository.findByEmail(email).ifPresent(ignored -> {
            throw new UserAlreadyExistsException("Email", String.format("User with email %s already exists", email.getValue()));
        });

        final NLUser user = new NLUser();
        user.setFirstName(name);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(password.getValue())));
        user.setRole(NLUserType.USER);
        user.setId(NLUuid.of(UUID.randomUUID()));
        return userRepository.save(user).getId();
    }

    @Override
    public void update(final UserUpdateRequest request) {
        if (ObjectUtils.isBlank(request)) {
            throw new InvalidUserDataException("Data", "Invalid email appKey secretKey or smtpAccount");
        }

        final NLEmail email = NLEmail.of(request.email());
        final NLAppKey appKey = NLAppKey.of(request.appKey());
        final NLSecretKey secretKey = NLSecretKey.of(request.secretKey());
        final NLSmtpAccount smtpAccount = NLSmtpAccount.of(request.smtpAccount());

        if (!email.validate()) {
            throw new InvalidUserDataException("ID", "Invalid");
        }

        if (!isDataOk(appKey, secretKey, smtpAccount)) {
            throw new InvalidUserDataException(String.format("Either appKey: %s, secretKey: %s or smtpAccount: %s could not be validated.", appKey.getValue(), secretKey.getValue(), smtpAccount.getValue()));
        }

        final Optional<NLUser> optionalNLUser = userRepository.findByEmail(email);
        if (optionalNLUser.isEmpty()) {
            throw new InvalidUserDataException();
        }

        final NLUser nlUser = optionalNLUser.get();
        nlUser.setAppKey(NLAppKey.of(hash(appKey.getValue())));
        nlUser.setSecretKey(NLSecretKey.of(hash(secretKey.getValue())));
        nlUser.setSmtpAccount(NLSmtpAccount.of(hash(smtpAccount.getValue())));
        userRepository.save(nlUser);
    }

    @Override
    public void delete(final UserDeleteRequest request) {
        if (ObjectUtils.isBlank(request)) {
            throw new InvalidUserDataException("Data", "Invalid data");
        }

        final NLUuid uuid = NLUuid.of(request.id());
        if (!uuid.validate() || !(NLPassword.of(request.password())).validate()) {
            throw new InvalidUserDataException();
        }

        final Optional<NLUser> optionalNLUser = userRepository.findById(uuid);
        if ((optionalNLUser.isEmpty())) {
            throw new InvalidUserDataException();
        }

        final NLUser user = optionalNLUser.get();
        if (!passwordEncoder.bCrypt().matches(request.password(), user.getPassword())) {
            throw new InvalidUserDataException();
        }

        userRepository.deleteById(user.getId());
    }

    private boolean isDataOk(final NLModel first, final NLModel second, final NLModel third) {
        return first.validate() && second.validate() && third.validate();
    }

    private @NotNull String hash(@NotNull final String password) {
        return passwordEncoder.encrypt(password);
    }
}
