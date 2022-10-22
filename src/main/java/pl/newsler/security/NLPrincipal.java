package pl.newsler.security;

import lombok.Getter;

import javax.security.auth.Subject;
import java.io.Serializable;
import java.security.Principal;

@SuppressWarnings("record")
public class NLPrincipal implements Principal, Serializable {
    @Getter
    private final long id;
    @Getter
    private final String email;
    @Getter
    private final String smtpAccount;
    @Getter
    private final String appKey;

    public NLPrincipal(long id, String email, String smtpAccount, String appKey) {
        this.id = id;
        this.email = email;
        this.smtpAccount = smtpAccount;
        this.appKey = appKey;
    }

    /**
     * Alias for {@link #getEmail()}
     *
     * @return email
     */
    @Override
    public String getName() {
        return email;
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NLPrincipal that = (NLPrincipal) o;

        if (id != that.id) {
            return false;
        }
        if (!email.equals(that.email)) {
            return false;
        }
        if (!smtpAccount.equals(that.smtpAccount)) {
            return false;
        }
        return appKey.equals(that.appKey);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + email.hashCode();
        result = 31 * result + smtpAccount.hashCode();
        result = 31 * result + appKey.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("{%n'id':%d,%n'email':%s,%n'smtpAccount':%s,%n'appKey':%s%n}", id, email, smtpAccount, appKey);
    }
}
