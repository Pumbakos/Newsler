package pl.newsler.security;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.newsler.commons.model.NLPassword;

import java.io.Serial;
import java.io.Serializable;

public class NLCredentials implements Serializable {
    @Serial
    private static final long serialVersionUID = -7419573552282464417L;

    @Getter
    private final NLPassword password;

    public NLCredentials(NLPassword password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final NLCredentials that = (NLCredentials) o;
        return new EqualsBuilder().append(password, that.password).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(password).toHashCode();
    }

    @Override
    public String toString() {
        return String.format("{%n'password':'%s'%n}", password);
    }
}
