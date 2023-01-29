package pl.newsler.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EmailAlreadyConfirmedException extends NLException {
    public EmailAlreadyConfirmedException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public EmailAlreadyConfirmedException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public EmailAlreadyConfirmedException() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.UNAUTHORIZED);
    }
}
