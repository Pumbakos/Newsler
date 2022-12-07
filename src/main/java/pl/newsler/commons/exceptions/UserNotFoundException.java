package pl.newsler.commons.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "")
@RequiredArgsConstructor
public class UserNotFoundException extends NLException {
    public ResponseEntity<NLError> response() {
        return new ResponseEntity<>(new NLError("", ""), HttpStatus.BAD_REQUEST);
    }
}
