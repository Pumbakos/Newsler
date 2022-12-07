package pl.newsler.security.filters;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import pl.newsler.testcommons.PleaseImplementMeException;

public class StubAuthenticationManager implements AuthenticationManager {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        throw new PleaseImplementMeException();
    }
}
