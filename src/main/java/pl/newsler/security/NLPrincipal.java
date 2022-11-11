package pl.newsler.security;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLName;

import javax.security.auth.Subject;
import java.io.Serializable;
import java.security.Principal;
import java.util.Objects;

public class NLPrincipal implements Principal, Serializable {
    @Getter
    private final NLId id;
    @Getter
    private final NLEmail email;

    private final NLName name;

    public NLPrincipal(NLId id, NLEmail email, NLName name) {
        this.id = id;
        this.email = email;
        this.name = name;
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

        return new EqualsBuilder().append(id, that.id).append(email, that.email).append(name, that.name).isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name);
    }

    @Override
    public String toString() {
        return String.format("{%n'id':'%s',%n'email':'%s',%n'name':'%s'%n}", id.getValue(), email.getValue(), name.getValue());
    }
}
