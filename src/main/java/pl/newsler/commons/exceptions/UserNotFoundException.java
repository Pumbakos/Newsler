package pl.newsler.commons.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserNotFoundException extends NLException {
    public UserNotFoundException() {
        super("", "");
    }

    public UserNotFoundException(String cause, String message) {
        super(cause, message);
    }

    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(new NLError(error, errorMessage), HttpStatus.BAD_REQUEST);
    }
}
