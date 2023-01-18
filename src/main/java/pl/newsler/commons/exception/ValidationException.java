package pl.newsler.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ValidationException extends NLException {
    public ValidationException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public ValidationException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public ValidationException() {
        super("Not provided", "Not specified");
    }

    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of("", ""), HttpStatus.BAD_REQUEST);
    }
}
