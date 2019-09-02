package com.calinfo.api.common.ex.handler;

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