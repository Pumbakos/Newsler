package pl.newsler.commons.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public abstract class NLException extends RuntimeException {
    protected final String error;
    protected final String message;

    public abstract ResponseEntity<NLError> response();
}
