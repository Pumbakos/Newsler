package pl.newsler.api.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.newsler.commons.exceptions.NLError;
import pl.newsler.components.user.UserDataNotFineException;


@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@ControllerAdvice(annotations = Advised.class)
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {
    public GlobalRestExceptionHandler() {
        super();
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody ResponseEntity<NLError> handleException(UnauthorizedException ex) {
        return ex.response();
    }

    @ExceptionHandler(UserDataNotFineException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ResponseEntity<NLError> handleException(UserDataNotFineException ex) {
        return ex.response();
    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        log.error("500 Status Code", ex);
        final String bodyOfResponse = "Sorry we currently we have problem processing your request";
        return new ResponseEntity<>(new NLError(ex.getCause().getMessage(), bodyOfResponse), HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    /**
     * Customize the handling of {@link MethodArgumentNotValidException}.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex      the exception to handle
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} for the response to use, possibly
     * {@code null} when the response is already committed
     */
    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>(new NLError(ex.getTitleMessageCode(), ex.getMessage()), HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
    }
}