package pl.newsler.commons.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j(topic = "GLOBAL_REST_EXCEPTION_HANDLER")
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalRestExceptionHandler {
    @ExceptionHandler
    public ProblemDetail handleException(@NotNull final NLException ex) {
        final ProblemDetail detail = ProblemDetail.forStatusAndDetail(ex.response().getStatusCode(), ex.errorMessage == null ? "Not provided" : ex.errorMessage);
        detail.setProperty(ex.error, ex.errorMessage);
        return detail;
    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Object> handleInternal(final RuntimeException ex) {
        log.error("500 Status Code", ex);
        final String bodyOfResponse = "Sorry we currently we have problem processing your request";
        return new ResponseEntity<>(NLError.of(ex.getCause().getMessage(), bodyOfResponse), HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}