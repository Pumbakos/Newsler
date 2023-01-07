package pl.newsler.security.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.newsler.api.exceptions.Advised;
import pl.newsler.commons.exceptions.NLError;
import pl.newsler.commons.exceptions.NLException;

@Advised
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Cipher key could not be initialized")
@RequiredArgsConstructor
public class AlgorithmInitializationException extends NLException {
    public AlgorithmInitializationException(String cause, String message) {
        super(cause, message);
    }

    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(new NLError(error, message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}