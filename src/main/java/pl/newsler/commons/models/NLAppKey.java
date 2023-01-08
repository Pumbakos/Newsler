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
public class NLAppKey implements NLModel {
    @Serial
    private static final long serialVersionUID = -6828434776883712910L;

    private final String value;

    public boolean validate() {
        return StringUtils.isNotBlank(value) && value.matches("^[a-zA-Z\\d]{40}$");
    }

    @Override
    public String toString() {
        return value;
    }
}
