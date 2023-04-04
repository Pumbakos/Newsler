package pl.newsler.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class NLAuthenticationToken extends AbstractAuthenticationToken {
    private final NLPrincipal principal;
    private final NLCredentials credentials;
    @Getter
    @Setter
    private boolean validated;

    public NLAuthenticationToken(NLPrincipal principal, NLCredentials credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        validated = false;
    }

    public NLAuthenticationToken(Authentication authentication) {
        super(authentication.getAuthorities());
        this.principal = (NLPrincipal) authentication.getPrincipal();
        this.credentials = (NLCredentials) authentication.getCredentials();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        NLAuthenticationToken that = (NLAuthenticationToken) o;
        if (!principal.equals(that.principal)) {
            return false;
        }
        return credentials.equals(that.credentials);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + principal.hashCode();
        result = 31 * result + credentials.hashCode();
        return result;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
