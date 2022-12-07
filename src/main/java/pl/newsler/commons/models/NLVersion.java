package pl.newsler.commons.models;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import pl.newsler.commons.exceptions.RegexNotMatchException;

import java.io.Serial;
import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class NLVersion implements Serializable {
    @Serial
    private static final long serialVersionUID = -5814295840525940021L;

    private final String value;

    public static NLVersion of(String version) {
        if (!version.matches("^\\d.\\d.\\d(?i)[a-z]*$")) {
            throw new RegexNotMatchException("version", "invalid format");
        }
        return new NLVersion(version);
    }

    @Override
    public String toString() {
        return value;
    }
}
