package pl.newsler.security.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import pl.newsler.security.NLAuthenticationToken;

public class StubAuthenticationManager implements AuthenticationManager {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return new NLAuthenticationToken(authentication);
    }
}
