package com.calinfo.api.common.ex.handler;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2023 CALINFO
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

import com.calinfo.api.common.dto.BadRequestParameterDto;
import com.calinfo.api.common.dto.BadResponseDto;
import com.calinfo.api.common.ex.BadRequestParameterException;
import com.calinfo.api.common.ex.MessageException;
import com.calinfo.api.common.service.MessageService;
import com.calinfo.api.common.utils.HandlerUtils;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public BadResponseDto messageException(MessageException ex) {
        return HandlerUtils.messageException(messageService, ex);
    }

}
