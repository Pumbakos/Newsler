package pl.newsler.commons.models;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import pl.newsler.commons.exceptions.ValidationException;

import java.io.Serial;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class NLId implements NLModel {
    @Serial
    private static final long serialVersionUID = -2838811969171019799L;

    private final String value;

    public static NLId of(final UUID uuid) {
        return of(uuid, NLUserType.USER);
    }

    public static NLId of(String id) {
        NLId nlId = NLId.of(fromString(id));
        if (!nlId.validate()) {
            throw new ValidationException("User ID", "Not validated");
        }
        return nlId;
    }

    public static NLId of(final UUID uuid, final NLUserType type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        return new NLId(String.format("%s_%s", type.getPrefix(), uuid.toString()));
    }

    public static NLId of(final UUID uuid, final NLIdType type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        return new NLId(String.format("%s_%s", type.getPrefix(), uuid.toString()));
    }

    public static NLId fromStringifyNLId(String id, NLIdType type) {
        try {
            return of(UUID.fromString(id.contains("_") ? id.split("_")[1] : id), type);
        } catch (Exception e) {
            throw new ValidationException();
        }
    }

    private static UUID fromString(String id) {
        try {
            return UUID.fromString(id.contains("_") ? id.split("_")[1] : id);
        } catch (Exception e) {
            throw new ValidationException();
        }
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
