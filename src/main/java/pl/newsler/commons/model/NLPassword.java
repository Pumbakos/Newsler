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
public class NLPassword implements NLModel {
    @Serial
    private static final long serialVersionUID = 9034287199490303946L;
    private final String value;

    public boolean validate() {
        return StringUtils.isNotBlank(value) && value.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{12,256}$");
    }

    @Override
    public String toString() {
        return value;
    }
}
