package pl.newsler.components.emaillabs.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.newsler.commons.exceptions.NLError;
import pl.newsler.commons.exceptions.NLException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Mail not found.")
@RequiredArgsConstructor
public class ELAMailNotFoundException extends NLException {
    private final String cause;
    private final String message;

    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(new NLError(cause, message), HttpStatus.UNAUTHORIZED);
    }
}
