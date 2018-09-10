package com.calinfo.api.common.kafka;

import lombok.Getter;

public class KafkaInvocationException extends KafkaException {


    public KafkaInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
