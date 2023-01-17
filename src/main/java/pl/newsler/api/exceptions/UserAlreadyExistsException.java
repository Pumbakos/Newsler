package pl.newsler.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.commons.exceptions.NLError;
import pl.newsler.commons.exceptions.NLException;

public class UserAlreadyExistsException extends NLException {
    public UserAlreadyExistsException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public UserAlreadyExistsException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public UserAlreadyExistsException() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.BAD_REQUEST);
    }
}