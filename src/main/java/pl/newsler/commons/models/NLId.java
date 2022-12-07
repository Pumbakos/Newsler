package pl.newsler.commons.models;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class NLId implements NLModel, Serializable {
    @Serial
    private static final long serialVersionUID = -2838811969171019799L;

    private final String value;

    public static NLId of(UUID uuid) {
        return of(uuid, NLType.USER);
    }

    public static NLId of(UUID uuid, NLType type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        return new NLId(String.format("%s_%s", type.getPrefix(), uuid.toString()));
    }

    @Override
    public boolean validate() {
        return value.matches("[a-z]{3}_[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}");
    }

    @Override
    public String toString() {
        return value;
    }
}
