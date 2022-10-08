package pl.newsler.security;

import lombok.Getter;

import java.io.Serializable;

@SuppressWarnings("record")
public class NLCredentials implements Serializable {
    public NLCredentials(String password, String secretKey) {
        this.password = password;
        this.secretKey = secretKey;
    }

    @Getter
    private final String password;
    @Getter
    private final String secretKey;

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
