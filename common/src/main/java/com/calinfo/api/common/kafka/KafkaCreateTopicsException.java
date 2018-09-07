package com.calinfo.api.common.kafka;

public class KafkaCreateTopicsException extends RuntimeException {

    public KafkaCreateTopicsException(Throwable cause) {
        super(cause);
    }

    public KafkaCreateTopicsException(String message, Throwable cause) {
        super(message, cause);
    }
}
