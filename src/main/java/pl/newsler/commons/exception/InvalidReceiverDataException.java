package pl.newsler.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class InvalidReceiverDataException extends NLException {
    public InvalidReceiverDataException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public InvalidReceiverDataException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public InvalidReceiverDataException() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.BAD_REQUEST);
    }
}
