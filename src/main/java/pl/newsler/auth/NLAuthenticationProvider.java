package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.security.NLAuthenticationToken;
import pl.newsler.security.NLPrincipal;

import java.util.Optional;

@RequiredArgsConstructor
public class NLAuthenticationProvider implements AuthenticationProvider {
    private final IUserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof NLAuthenticationToken token && token.isValidated()) {
            authentication.setAuthenticated(true);
            return token;
        }

        final NLPrincipal principal = (NLPrincipal) authentication.getPrincipal();
        final Optional<NLUser> optionalUser = userRepository.findByEmail(principal.getEmail());

        if (optionalUser.isPresent()) {
            final NLAuthenticationToken token = new NLAuthenticationToken(authentication);
            token.setAuthenticated(true);
            token.setValidated(true);
            return token;
        }
        throw new BadCredentialsException("Incorrect credentials. Not authenticated.");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(NLAuthenticationToken.class);
    }
}