package pl.newsler.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class InvalidTokenException extends NLException {
    public InvalidTokenException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public InvalidTokenException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public InvalidTokenException() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.UNAUTHORIZED);
    }
}
