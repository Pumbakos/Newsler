package pl.palubiak.dawid.newsler.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.businesclinet.repository.BusinessClientRepository;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.user.model.requestmodel.RequestUser;
import pl.palubiak.dawid.newsler.user.model.requestmodel.UserRequest;
import pl.palubiak.dawid.newsler.user.registration.ConfirmationToken;
import pl.palubiak.dawid.newsler.user.repository.UserRepository;
import pl.palubiak.dawid.newsler.utils.UpdateUtils;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private static final String USER_NOT_FOUND_MSG = "User with email: %s not found";
    private final UserRepository userRepository;
    private final BusinessClientRepository businessClientRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Autowired
    public UserService(UserRepository userRepository, BusinessClientRepository businessClientRepository, BCryptPasswordEncoder bCryptPasswordEncoder, ConfirmationTokenService confirmationTokenService) {
        this.userRepository = userRepository;
        this.businessClientRepository = businessClientRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.confirmationTokenService = confirmationTokenService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(@NotNull Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    /**
     * @deprecated
     * @since 14-06-2022
     */
    @Deprecated(forRemoval = true, since = "14-06-2022")
    public Optional<User> save(RequestUser requestUser) {
        if (!requestUser.isValid()) {
            return Optional.empty();
        }

        User user = new User();
        user.setEmail(requestUser.getEmail());
        user.setName(requestUser.getName());
        user.setPassword(requestUser.getPassword());

        return Optional.of(userRepository.save(user));
    }

    public boolean update(@NotNull long id, UserRequest user) {
        final Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent() && user.password().contentEquals(user.password())) {
            final User userOp = byId.get();
            userOp.setName(user.name());
            userOp.setLastName(user.lastName());
            userOp.setEmail(user.email());
            userOp.setAppKey(user.appKey());
            userOp.setSecretKey(user.secretKey());
            userOp.setSmtpAccount(user.smtpAccount());
            userRepository.save(userOp);
            return true;
        }
        return false;
    }

    public boolean delete(@NotNull long id) {
        Optional<User> byId = userRepository.findById(id);
        byId.ifPresent(userRepository::delete);
        return byId.isPresent();
    }

    public boolean addBusinessClient(@NotNull long userId, BusinessClient client) {
        if (!isBusinessClientValid(client)) {
            throw new IllegalArgumentException("Email and name must not be blank");
        }

        Optional<User> byId = userRepository.findById(userId);
        if (byId.isEmpty()) {
            throw new IllegalArgumentException("User with id " + userId + " does not exist");
        }

        BusinessClient businessClient = new BusinessClient();
        businessClient.setEmail(client.getEmail());
        businessClient.setName(client.getName());
        businessClient.setEmailType(client.getEmailType());
        businessClient.setUser(byId.get());

        if (!client.getLastName().isBlank()) {
            businessClient.setLastName(client.getLastName());
        }
        businessClientRepository.save(businessClient);

        return true;
    }

    public boolean isBusinessClientValid(BusinessClient client) {
        return !client.getEmail().isBlank() && !client.getName().isBlank();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG, email)
                        )
                );
    }

    public String singUp(User user) {
        boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();
        //TODO: if email not confirmed send confirmation email
        if (userExists) {
            throw new IllegalStateException("Email already taken");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        String token = generateToken();
        ConfirmationToken confirmationToken = getConfirmationToken(user, token);

        confirmationTokenService.save(confirmationToken);

        return token;
    }

    public ConfirmationToken getConfirmationToken(User user, String token) {
        return new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15L),
                user
        );
    }

    public void enableUser(String email) {
        userRepository.enableUser(email);
    }

    public Optional<User> getUserByEmailAndPassword(String email, String password) {
        final List<User> all = userRepository.findAll();
        final Optional<User> optionalUser = all.stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return bCryptPasswordEncoder.matches(password, user.getPassword()) ? Optional.of(user) : Optional.empty();
        }
        return Optional.empty();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
