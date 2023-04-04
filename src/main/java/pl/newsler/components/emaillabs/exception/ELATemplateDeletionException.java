package pl.newsler.components.emaillabs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.commons.exception.NLError;
import pl.newsler.commons.exception.NLException;

public class ELATemplateDeletionException extends NLException {
    public ELATemplateDeletionException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public ELATemplateDeletionException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public ELATemplateDeletionException() {
        super("Not provided", "Not specified");
    }

    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(error, errorMessage), HttpStatus.BAD_REQUEST);
    }
}