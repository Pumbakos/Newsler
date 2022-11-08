package pl.newsler.commons.models;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
@EqualsAndHashCode
public class NLId implements NLModel, Serializable {
    @Serial
    private static final long serialVersionUID = -2838811969171019799L;

    private final String value;

    public static NLId of(UUID uuid) {
        return of(uuid, NLType.USER);
    }

    public static NLId of(UUID uuid, NLType type) {
        return new NLId(String.format("%s_%s", type.getPrefix(), uuid.toString()));
    }

    @Override
    public boolean validate() {
        return StringUtils.isNotBlank(value);
    }
}
