package pl.newsler.commons.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC, staticName = "of")
@EqualsAndHashCode
public class NLSubject implements NLModel {
    @Serial
    private static final long serialVersionUID = 1201228337290338507L;

    private final String value;

    @Override
    public boolean validate() {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return value.length() < 129;
    }
}
