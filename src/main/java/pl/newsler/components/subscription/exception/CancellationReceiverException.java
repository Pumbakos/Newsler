package pl.newsler.components.subscription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.commons.exception.NLError;
import pl.newsler.commons.exception.NLException;

public class CancellationReceiverException extends NLException {
    public CancellationReceiverException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public CancellationReceiverException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public CancellationReceiverException() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.BAD_REQUEST);
    }
}
