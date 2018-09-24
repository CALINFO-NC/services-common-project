package com.calinfo.api.common.ex;

import lombok.Getter;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * Lorsq'une contrainte de violation est levée par programmation il n'est pas possible
 * de définir un préfix sur la propriété sur laquelle la contrainte est levée.
 * Cette exception permet définir ce préfix lors de la restitution dans{@link com.calinfo.api.common.ex.handler.RestResponseEntityExceptionHandler#constraintViolationException}
 */
public class CommonConstraintViolationException extends ConstraintViolationException {

    @Getter
    private final String propertyPrefix;

    public CommonConstraintViolationException(String propertyPrefix, String message, Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(message, constraintViolations);
        this.propertyPrefix = propertyPrefix;
    }

    public CommonConstraintViolationException(String propertyPrefix, Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(constraintViolations);
        this.propertyPrefix = propertyPrefix;
    }
}
