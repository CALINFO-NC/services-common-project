package com.calinfo.api.common.kafka.processor;

/**
 * Created by dalexis on 08/05/2018.
 */
public class ProcessorRuntimeException extends RuntimeException {
    public ProcessorRuntimeException(Throwable cause) {
        super(cause);
    }
}
