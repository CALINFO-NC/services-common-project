package com.calinfo.api.common.kafka;

import lombok.Getter;

public class KafkaException extends RuntimeException {

    @Getter
    private String originalCauseClassName;

    @Getter
    private boolean originalCauseException;

    public KafkaException(String message, Throwable cause, boolean originalCauseException) {
        super(message, cause);
        this.originalCauseClassName = message;
        this.originalCauseException = originalCauseException;
    }
}
