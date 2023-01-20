package pl.newsler.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class DecryptionException extends NLException {
    public DecryptionException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public DecryptionException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public DecryptionException() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.UNAUTHORIZED);
    }
}