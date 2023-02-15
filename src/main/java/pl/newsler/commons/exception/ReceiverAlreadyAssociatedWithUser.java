package pl.newsler.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ReceiverAlreadyAssociatedWithUser extends NLException {
    public ReceiverAlreadyAssociatedWithUser(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public ReceiverAlreadyAssociatedWithUser(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public ReceiverAlreadyAssociatedWithUser() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.BAD_REQUEST);
    }
}
