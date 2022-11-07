package pl.newsler.testcommons;

public class PleaseImplementMeException extends RuntimeException {
    public PleaseImplementMeException() {
        super();
    }

    public PleaseImplementMeException(String message) {
        super(message);
    }

    public PleaseImplementMeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PleaseImplementMeException(Throwable cause) {
        super(cause);
    }

    protected PleaseImplementMeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
