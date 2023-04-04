package pl.newsler.components.emaillabs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.commons.exception.NLError;
import pl.newsler.commons.exception.NLException;

public class ELASendMailException extends NLException {
    public ELASendMailException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public ELASendMailException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public ELASendMailException() {
        super("Not provided", "Not specified");
    }

    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(error, errorMessage), HttpStatus.BAD_REQUEST);
    }
}
