package pl.newsler.security;

import lombok.Getter;
import pl.newsler.commons.models.Email;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.components.user.NLId;

import javax.security.auth.Subject;
import java.io.Serializable;
import java.security.Principal;

@SuppressWarnings("record")
public class NLPrincipal implements Principal, Serializable {
    @Getter
    private final NLId id;
    @Getter
    private final Email email;
    @Getter
    private final NLSmtpAccount smtpAccount;
    @Getter
    private final NLAppKey appKey;

    public NLPrincipal(NLId id, Email email, NLSmtpAccount smtpAccount, NLAppKey appKey) {
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
        return email.getValue();
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
        int result = (int) (id.getValue() ^ (id.getValue() >>> 32));
        result = 31 * result + email.hashCode();
        result = 31 * result + smtpAccount.hashCode();
        result = 31 * result + appKey.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("{%n'id':%d,%n'email':%s,%n'smtpAccount':%s,%n'appKey':%s%n}", id.getValue(), email.getValue(), smtpAccount.getValue(), appKey.getValue());
    }
}
