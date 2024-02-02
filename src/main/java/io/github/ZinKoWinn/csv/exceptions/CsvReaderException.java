package io.github.zinkowinn.csv.exceptions;

/**
 * @author Zin Ko Winn
 */

public class CsvReaderException extends RuntimeException {

    public CsvReaderException(String message) {
        super(message);
    }

    public CsvReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvReaderException(Throwable cause) {
        super(cause);
    }

    public CsvReaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
