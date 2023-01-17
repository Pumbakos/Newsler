package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.api.exceptions.UserDataNotFineException;

import java.util.Optional;

@RequiredArgsConstructor
public class AuthUserDetailService implements UserDetailsService {
    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        NLEmail nlEmail = NLEmail.of(email);
        if (!nlEmail.validate()) {
            throw new UserDataNotFineException();
        }

        Optional<NLUser> optionalUser = userRepository.findByEmail(nlEmail);
        if (optionalUser.isEmpty()) {
            throw new UserDataNotFineException();
        }

        return optionalUser.get();
    }
}