package com.calinfo.api.common.ex.handler;

import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ResponseEntityExceptionHandler {


    private static final Logger log = LoggerFactory.getLogger(ResponseEntityExceptionHandler.class);

    @ExceptionHandler(MessageStatusException.class)
    public ResponseEntity<String> messageStatusException(MessageStatusException ex) {

        log.info(ex.getMessage(), ex);

        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> messageThrowable(Throwable ex) {

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(ExceptionUtils.getPrintValue(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}