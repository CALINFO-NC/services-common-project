package com.calinfo.api.common.ex;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
