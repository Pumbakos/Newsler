package pl.newsler.commons.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import pl.newsler.commons.exception.ValidationException;

import java.io.Serial;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class NLId implements NLModel {
    @Serial
    private static final long serialVersionUID = 5202504447274656522L;
    private final long value;

    public static NLId of(final long id) {
        return new NLId(id);
    }

    public static NLId of(String id) {
        NLId nlUuid = fromString(id);
        if (!nlUuid.validate()) {
            throw new ValidationException("User ID", "Not validated");
        }
        return nlUuid;
    }

    private static NLId fromString(String id) {
        try {
            return of(Long.parseLong(id));
        } catch (Exception e) {
            throw new ValidationException();
        }
    }

    @Override
    public boolean validate() {
        return value > 0;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
