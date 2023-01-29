package pl.newsler.commons.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public abstract class NLException extends RuntimeException {
    protected final String error;
    protected final String errorMessage;

    public abstract ResponseEntity<NLError> response();
}
