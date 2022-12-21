package pl.newsler.commons.models;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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
        return value.length() < 129;
    }
}
