package pl.newsler.exceptions;

import org.springframework.http.ResponseEntity;

public abstract class NLException extends RuntimeException {
    protected abstract ResponseEntity<NLError> response();
}
