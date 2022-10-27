package pl.newsler.security;

import lombok.Getter;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.Password;

import java.io.Serial;
import java.io.Serializable;

@SuppressWarnings("record")
public class NLCredentials implements Serializable {
    @Serial
    private static final long serialVersionUID = -7419573552282464417L;

    public NLCredentials(String password, NLSecretKey secretKey) {
        this.password = Password.of(password);
        this.secretKey = secretKey;
    }

    @Getter
    private final Password password;
    @Getter
    private final NLSecretKey secretKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NLCredentials that = (NLCredentials) o;
        if (!password.equals(that.password)) {
            return false;
        }
        return secretKey.equals(that.secretKey);
    }

    @Override
    public int hashCode() {
        int result = password.hashCode();
        result = 31 * result + secretKey.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("{%n'password':%s,%n'secretKey':%s%n}", password, secretKey);
    }
}
