package com.calinfo.api.common.ex.handler;

import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ResponseEntityExceptionHandler {


    private static final Logger log = LoggerFactory.getLogger(ResponseEntityExceptionHandler.class);

    @ExceptionHandler(MessageStatusException.class)
    public ResponseEntity<String> messageStatusException(MessageStatusException ex) {

        log.info(ex.getMessage(), ex);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>(ex.getMessage(), header, ex.getStatus());
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> messageAccessDeniedException(AccessDeniedException ex) {

        log.info(ex.getMessage(), ex);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>(ex.getMessage(), header, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> messageThrowable(Throwable ex) {

        log.error(ex.getMessage(), ex);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>(ExceptionUtils.getPrintValue(ex), header, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}