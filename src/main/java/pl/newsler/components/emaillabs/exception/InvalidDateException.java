package pl.newsler.components.emaillabs.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.newsler.commons.exception.NLError;
import pl.newsler.commons.exception.NLException;

@RequiredArgsConstructor
public class InvalidDateException extends NLException {
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(NLError.of(super.error, super.errorMessage), HttpStatus.BAD_REQUEST);
    }
}
