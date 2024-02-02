package io.github.zinkowinn.csv.exceptions;

public class IllegalCastException extends RuntimeException {
    public IllegalCastException() {
    }

    public IllegalCastException(String message) {
        super(message);
    }

    public IllegalCastException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalCastException(Throwable cause) {
        super(cause);
    }

    public IllegalCastException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
