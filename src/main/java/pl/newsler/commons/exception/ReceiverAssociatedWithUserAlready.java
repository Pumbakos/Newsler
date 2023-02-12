package pl.newsler.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ReceiverAssociatedWithUserAlready extends NLException {
    public ReceiverAssociatedWithUserAlready(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public ReceiverAssociatedWithUserAlready(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public ReceiverAssociatedWithUserAlready() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.BAD_REQUEST);
    }
}
