package pl.palubiak.dawid.newsler.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.businesclinet.repository.BusinessClientRepository;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.user.repository.UserRepository;
import pl.palubiak.dawid.newsler.utils.UpdateUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UpdateUtils<User> updateUtils;
    private final BusinessClientRepository businessClientRepository;

    @Autowired
    public UserService(UserRepository userRepository, UpdateUtils<User> updateUtils, BusinessClientRepository businessClientRepository) {
        this.userRepository = userRepository;
        this.updateUtils = updateUtils;
        this.businessClientRepository = businessClientRepository;
    }

    public User save(@NotBlank String email, @NotBlank String name, @NotBlank String password) {
        if (email.isBlank() || name.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Email, name and password must not be blank");
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);

        return userRepository.save(user);
    }

    public boolean update(@NotNull long id, User user) {
        return updateUtils.update(userRepository, user, id);
    }

    public boolean delete(@NotNull long id) {
        Optional<User> byId = userRepository.findById(id);
        byId.ifPresent(userRepository::delete);
        return byId.isPresent();
    }

    public boolean addBusinessClient(@NotNull long userId, @NotBlank String email, @NotBlank String name) {
        if (email.isBlank() || name.isBlank()) {
            throw new IllegalArgumentException("Email and name must not be blank");
        }

        Optional<User> byId = userRepository.findById(userId);
        if (byId.isEmpty()){
            throw new IllegalArgumentException("User with id " + userId + " does not exist");
        }

        BusinessClient businessClient = new BusinessClient();
        businessClient.setEmail(email);
        businessClient.setName(name);
        businessClient.setUser(byId.get());

        businessClientRepository.save(businessClient);

        return true;
    }
}
