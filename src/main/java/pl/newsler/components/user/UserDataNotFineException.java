package pl.newsler.components.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.commons.exceptions.NLError;
import pl.newsler.commons.exceptions.NLException;

public class UserDataNotFineException extends NLException {
    public UserDataNotFineException(String cause, String message) {
        super(cause, message);
    }

    public UserDataNotFineException(String message) {
        super("User data are incorrect", message);
    }

    public UserDataNotFineException() {
        super("User data are incorrect", "");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(new NLError(error, message), HttpStatus.BAD_REQUEST);
    }
}
