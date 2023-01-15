package pl.newsler.components.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.commons.exceptions.NLError;
import pl.newsler.commons.exceptions.NLException;

public class UserDataNotFineException extends NLException {
    public UserDataNotFineException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public UserDataNotFineException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public UserDataNotFineException() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.BAD_REQUEST);
    }
}
