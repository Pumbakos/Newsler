package pl.newsler.commons.models;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode
public class NLStringValue implements Serializable {
    @Serial
    private static final long serialVersionUID = -5017051407745618377L;

    public static NLStringValue of(String value) {
        return new NLStringValue(value);
    }

    public static NLStringValue of(String[] values) {
        return of(Arrays.toString(values));
    }

    private final String value;
}
