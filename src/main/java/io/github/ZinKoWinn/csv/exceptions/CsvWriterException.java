package io.github.zinkowinn.csv.exceptions;

/**
 * @author Zin Ko Winn
 */

public class CsvWriterException extends RuntimeException {

    public CsvWriterException(String message) {
        super(message);
    }

    public CsvWriterException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvWriterException(Throwable cause) {
        super(cause);
    }

    public CsvWriterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
