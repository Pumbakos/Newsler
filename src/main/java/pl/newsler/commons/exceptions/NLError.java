package pl.newsler.commons.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NLError {
    private final String cause;
    private final String message;

    @Override
    public String toString() {
        return String.format("{\"cause\": %s%n,\"message\": %s%n}", cause, message);
    }
}
