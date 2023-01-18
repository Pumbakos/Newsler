package pl.newsler.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EmailCouldNotSentException extends NLException {
    public EmailCouldNotSentException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public EmailCouldNotSentException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public EmailCouldNotSentException() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.BAD_REQUEST);
    }
}
