package com.calinfo.api.common.ex.handler;

import com.calinfo.api.common.dto.BadRequestParameterDto;
import com.calinfo.api.common.dto.BadResponseDto;
import com.calinfo.api.common.ex.BadRequestParameterException;
import com.calinfo.api.common.ex.MessageException;
import com.calinfo.api.common.service.MessageService;
import com.calinfo.api.common.utils.HandlerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestResponseEntityExceptionHandler {

    @Autowired
    private MessageService messageService;



    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BadRequestParameterDto missingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return HandlerUtils.missingServletRequestParameterException(ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BadRequestParameterDto constraintViolationException(ConstraintViolationException ex) {
        return HandlerUtils.constraintViolationException(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BadRequestParameterDto methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return HandlerUtils.methodArgumentNotValidException(ex);
    }

    @ExceptionHandler(BadRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BadRequestParameterDto badRequestParameterException(BadRequestParameterException ex) {
        return HandlerUtils.badRequestParameterException(ex);
    }

    @ExceptionHandler(MessageException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public BadResponseDto messageException(MessageException ex) {
        return HandlerUtils.messageException(messageService, ex);
    }

}