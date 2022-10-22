package pl.newsler.components.user;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
public interface IUserService extends UserDetailsService {
    List<NLUser> findAll();

    NLUser findById(@NotNull Long id);

    boolean update(@NotNull long id, UserRequest user);

    boolean delete(@NotNull long id);

    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    String singUp(UserRequest user);

    //ConfirmationToken getConfirmationToken(User user, String token);

    void enableUser(String email);

    Optional<NLUser> getUserByEmailAndPassword(String email, String password);

    String generateToken();
}