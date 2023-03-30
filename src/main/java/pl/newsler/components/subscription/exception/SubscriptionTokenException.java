package pl.newsler.components.subscription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.commons.exception.NLError;
import pl.newsler.commons.exception.NLException;

public class SubscriptionTokenException extends NLException {
    public SubscriptionTokenException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public SubscriptionTokenException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public SubscriptionTokenException() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.BAD_REQUEST);
    }
}
