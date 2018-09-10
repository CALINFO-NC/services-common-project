package com.calinfo.api.common.kafka;

import lombok.Getter;

public class KafkaException extends RuntimeException {

    @Getter
    private final String originalCauseClassName;

    public KafkaException(String message, Throwable cause) {
        super(message, cause);
        this.originalCauseClassName = message;
    }
}
