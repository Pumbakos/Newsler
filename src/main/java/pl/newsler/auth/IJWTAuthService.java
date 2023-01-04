package pl.newsler.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.newsler.components.user.UserDataNotFineException;

public interface IJWTAuthService {
    String generateJWT(UserAuthModel userAuthModel) throws IllegalArgumentException, UserDataNotFineException;
}
