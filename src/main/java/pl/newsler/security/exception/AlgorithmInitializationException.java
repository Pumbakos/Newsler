package pl.newsler.security.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.commons.exception.NLError;
import pl.newsler.commons.exception.NLException;

@RequiredArgsConstructor
public class AlgorithmInitializationException extends NLException {
    public AlgorithmInitializationException(String cause, String message) {
        super(cause, message);
    }

    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(error, errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}