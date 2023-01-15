package pl.newsler.api.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.commons.exceptions.NLError;
import pl.newsler.commons.exceptions.NLException;

@RequiredArgsConstructor
public class UnauthorizedException extends NLException {
    private final String cause;
    private final String message;

    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(cause, message), HttpStatus.UNAUTHORIZED);
    }
}
