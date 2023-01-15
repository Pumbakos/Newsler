package pl.newsler.commons.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserNotFoundException extends NLException {
    public UserNotFoundException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public UserNotFoundException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public UserNotFoundException() {
        super("Not provided", "Not specified");
    }

    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(error, errorMessage), HttpStatus.BAD_REQUEST);
    }
}
