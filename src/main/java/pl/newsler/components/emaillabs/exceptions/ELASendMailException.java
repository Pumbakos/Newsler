package pl.newsler.components.emaillabs.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.newsler.commons.exceptions.NLError;
import pl.newsler.commons.exceptions.NLException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Check data, it is likely that SMTP, APP KEY or SECRET KEY are incorrect.")
@RequiredArgsConstructor
public class ELASendMailException extends NLException {
    private final String cause;
    private final String message;

    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(new NLError(cause, message), HttpStatus.UNAUTHORIZED);
    }
}
