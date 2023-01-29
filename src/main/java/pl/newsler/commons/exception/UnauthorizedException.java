package pl.newsler.commons.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
public class UnauthorizedException extends NLException {
    private final String cause;
    private final String message;

    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(cause, message), HttpStatus.UNAUTHORIZED);
    }
}