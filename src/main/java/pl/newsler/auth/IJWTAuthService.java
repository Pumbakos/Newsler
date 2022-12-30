package pl.newsler.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.newsler.components.user.UserDataNotFineException;

public interface IJWTAuthService extends UserDetailsService {
    String generateJWT(UserAuthModel userAuthModel) throws IllegalArgumentException, UserDataNotFineException;

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
