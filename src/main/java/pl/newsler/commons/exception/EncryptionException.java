package pl.newsler.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EncryptionException extends NLException {
    public EncryptionException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public EncryptionException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public EncryptionException() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.UNAUTHORIZED);
    }
}