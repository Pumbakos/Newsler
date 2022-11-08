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

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class NLLastName implements NLModel, Serializable {
    @Serial
    private static final long serialVersionUID = 499088190913111003L;

    private final String value;

    public boolean validate() {
        return StringUtils.isNotBlank(value) && value.matches("(?i)[a-z]([- ',.a-z]{0,23}[a-z])?");
    }
}