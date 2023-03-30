package pl.newsler.components.receiver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.commons.exception.NLError;
import pl.newsler.commons.exception.NLException;

public class ReceiverAlreadySubscribedException extends NLException {
    public ReceiverAlreadySubscribedException(String error, String errorMessage) {
        super(error, errorMessage);
    }

    public ReceiverAlreadySubscribedException(String errorMessage) {
        super("Not provided", errorMessage);
    }

    public ReceiverAlreadySubscribedException() {
        super("Not provided", "Not specified");
    }

    @Override
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.NO_CONTENT);
    }
}
