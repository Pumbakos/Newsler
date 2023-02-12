package pl.newsler.commons.models;

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
public class NLNickname implements NLName, NLModel {
    @Serial
    private static final long serialVersionUID = -1488710253723326176L;
    private final String value;

    @Override
    public boolean validate() {
        return StringUtils.isNotBlank(value) && value.matches("(?i)[a-z]([a-z]{0,23}[a-z])?");
    }

    @Override
    public String toString() {
        return value;
    }
}
