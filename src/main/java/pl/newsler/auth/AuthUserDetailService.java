package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;

import java.util.Optional;

@RequiredArgsConstructor
public class AuthUserDetailService implements UserDetailsService {

    public static final String AUTHORITIES_CLAIM_NAME = "roles";
    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final NLEmail nlEmail = NLEmail.of(email);
        if (!nlEmail.validate()) {
            throw new InvalidUserDataException();
        }

        final Optional<NLUser> optionalUser = userRepository.findByEmail(nlEmail);
        if (optionalUser.isEmpty()) {
            throw new InvalidUserDataException();
        }

        return optionalUser.get();
    }
}