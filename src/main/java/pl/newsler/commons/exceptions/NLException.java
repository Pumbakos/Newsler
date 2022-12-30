package pl.newsler.commons.exceptions;

import org.springframework.http.ResponseEntity;

public abstract class NLException extends RuntimeException {
    public abstract ResponseEntity<NLError> response();
}
