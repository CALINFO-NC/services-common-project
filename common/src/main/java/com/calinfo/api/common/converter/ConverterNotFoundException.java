package com.calinfo.api.common.converter;

/**
 * Created by dalexis on 19/05/2018.
 */
public class ConverterNotFoundException extends RuntimeException {
    public ConverterNotFoundException() {
    }

    public ConverterNotFoundException(String message) {
        super(message);
    }

    public ConverterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConverterNotFoundException(Throwable cause) {
        super(cause);
    }

    public ConverterNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
