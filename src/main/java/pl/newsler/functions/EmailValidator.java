package pl.newsler.functions;

import org.springframework.stereotype.Component;

@Component
public class EmailValidator {
    public static final String EMAIL_PATTERN = "[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param s the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    public boolean validate(String s) {
        return s.matches(EMAIL_PATTERN);
    }
}
