package pl.newsler.auth.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.newsler.exceptions.NLError;
import pl.newsler.exceptions.NLException;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Could not finalize decryption process")
@RequiredArgsConstructor
public class DecryptionException extends NLException {
    private final String cause;
    private final String message;

    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(new NLError(cause, message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}