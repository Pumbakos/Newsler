package pl.newsler.components.user;

public class UserDataNotFineException extends RuntimeException {
    public UserDataNotFineException() {
        super();
    }

    public UserDataNotFineException(String message) {
        super(message);
    }

    public UserDataNotFineException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDataNotFineException(Throwable cause) {
        super(cause);
    }

    protected UserDataNotFineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
