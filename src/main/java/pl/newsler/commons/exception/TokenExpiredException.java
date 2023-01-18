package pl.newsler.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class TokenExpiredException extends NLException {
    public TokenExpiredException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public TokenExpiredException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public TokenExpiredException() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.UNAUTHORIZED);
    }
}