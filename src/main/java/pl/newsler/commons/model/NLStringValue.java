package pl.newsler.commons.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Arrays;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode
public class NLStringValue implements NLModel {
    @Serial
    private static final long serialVersionUID = -5017051407745618377L;
    private final String value;

    public static NLStringValue of(String value) {
        return new NLStringValue(value);
    }

    public static NLStringValue of(String[] values) {
        return of(Arrays.toString(values));
    }

    @Override
    public boolean validate() {
        return true;
    }
}
