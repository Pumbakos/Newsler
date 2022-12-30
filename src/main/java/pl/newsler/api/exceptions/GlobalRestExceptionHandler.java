package pl.newsler.api.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.newsler.commons.exceptions.NLError;
import pl.newsler.commons.exceptions.NLException;
import pl.newsler.commons.exceptions.ValidationException;
import pl.newsler.components.user.UserDataNotFineException;

@ControllerAdvice
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {
    public GlobalRestExceptionHandler() {
        super();
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody ResponseEntity<NLError> handleException(UnauthorizedException ex) {
        return ex.response();
    }

    @ExceptionHandler(value = {UserDataNotFineException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ResponseEntity<NLError> handleException(NLException ex) {
        return ex.response();
    }

    @ExceptionHandler({ NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        logger.error("500 Status Code", ex);
        final String bodyOfResponse = "Sorry we currently we have problem processing your request";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}