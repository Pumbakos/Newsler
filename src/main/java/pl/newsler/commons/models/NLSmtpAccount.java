package pl.newsler.commons.models;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC, staticName = "of")
@EqualsAndHashCode
public class NLSmtpAccount implements NLModel, Serializable {
    @Serial
    private static final long serialVersionUID = -8440638731256566536L;

    private final String value;

    @Override
    public boolean validate() {
        return StringUtils.isNotBlank(value) && value.matches("^[0-9][.][a-z]{3,}[.]smtp$");
    }

    @Override
    public String toString() {
        return value;
    }
}
