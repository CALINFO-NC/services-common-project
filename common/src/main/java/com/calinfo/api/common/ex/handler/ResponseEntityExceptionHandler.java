package com.calinfo.api.common.ex.handler;

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

import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.utils.HandlerUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;


@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ResponseEntityExceptionHandler {

    @ExceptionHandler(MessageStatusException.class)
    public ResponseEntity<String> messageStatusException(MessageStatusException ex) {
        return HandlerUtils.messageStatusException(ex);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noSuchElementException(NoSuchElementException ex) {
        return HandlerUtils.noSuchElementException(ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> messageAccessDeniedException(AccessDeniedException ex) {
        return HandlerUtils.messageAccessDeniedException(ex);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> messageThrowable(Throwable ex) {
        return HandlerUtils.messageThrowable(ex);
    }
}
