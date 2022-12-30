package pl.newsler.components.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.commons.exceptions.NLError;
import pl.newsler.commons.exceptions.NLException;

public class UserDataNotFineException extends NLException {
    private final String cause;
    private final String message;

    public UserDataNotFineException(String cause, String message) {
        this.cause = cause;
        this.message = message;
    }

    public UserDataNotFineException(String message) {
        this.cause = "User data are incorrect";
        this.message = message;
    }

    public UserDataNotFineException() {
        this.cause = "User data are incorrect";
        this.message = "";
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(new NLError(cause, message), HttpStatus.BAD_REQUEST);
    }
}
