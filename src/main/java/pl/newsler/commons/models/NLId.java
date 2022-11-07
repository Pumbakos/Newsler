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

    public static NLId of(UUID uuid, NLType type) {
        return switch (type) {
            case USER -> new NLId("usr_" + uuid.toString());
            case ADMIN -> new NLId("adm_" + uuid.toString());
        };
    }

    @Override
    public boolean validate() {
        return value != null && StringUtils.isNotBlank(value.toString());
    }
}
