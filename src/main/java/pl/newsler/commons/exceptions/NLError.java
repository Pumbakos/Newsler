package pl.newsler.commons.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class NLError {
    private final String cause;
    private final String message;

    @Override
    public String toString() {
        return String.format("{\"cause\": %s%n,\"message\": %s%n}", cause, message);
    }
}
