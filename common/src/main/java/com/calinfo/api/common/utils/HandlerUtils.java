package com.calinfo.api.common.utils;

import com.calinfo.api.common.dto.AttributDto;
import com.calinfo.api.common.dto.BadRequestParameterDto;
import com.calinfo.api.common.dto.BadResponseDto;
import com.calinfo.api.common.ex.BadRequestParameterException;
import com.calinfo.api.common.ex.CommonConstraintViolationException;
import com.calinfo.api.common.ex.MessageException;
import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.ex.handler.ResponseEntityExceptionHandler;
import com.calinfo.api.common.service.MessageService;
import com.calinfo.api.common.type.TypeAttribut;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HandlerUtils {

    private static final Logger log = LoggerFactory.getLogger(ResponseEntityExceptionHandler.class);

    public static ResponseEntity<String> messageStatusException(MessageStatusException ex) {

        log.info(ex.getMessage(), ex);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>(ex.getMessage(), header, ex.getStatus());
    }

    public static ResponseEntity<String> noSuchElementException(NoSuchElementException ex) {

        log.info(ex.getMessage(), ex);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>(ex.getMessage(), header, HttpStatus.NOT_FOUND);
    }


    public static ResponseEntity<String> messageAccessDeniedException(AccessDeniedException ex) {

        log.info(ex.getMessage(), ex);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>(ex.getMessage(), header, HttpStatus.FORBIDDEN);
    }

    public static ResponseEntity<String> messageThrowable(Throwable ex) {

        log.error(ex.getMessage(), ex);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>(ExceptionUtils.getPrintValue(ex), header, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public static BadRequestParameterDto missingServletRequestParameterException(MissingServletRequestParameterException ex) {

        log.info(ex.getMessage(), ex);

        BadRequestParameterDto result = new BadRequestParameterDto();

        AttributDto attribut = new AttributDto();
        result.getListErrorMessages().add(attribut);
        attribut.setType(TypeAttribut.REQUEST);
        attribut.setName(ex.getParameterName());
        attribut.getListMessages().add(ex.getMessage());

        return result;
    }

    public static BadRequestParameterDto constraintViolationException(ConstraintViolationException ex) {

        log.info(ex.getMessage(), ex);

        BadRequestParameterDto result = new BadRequestParameterDto();

        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

        for (ConstraintViolation<?> contraintes : constraintViolations){

            AttributDto attribut = new AttributDto();
            result.getListErrorMessages().add(attribut);
            attribut.setType(TypeAttribut.RESOURCE);

            String name = contraintes.getPropertyPath().toString();

            if (contraintes.getPropertyPath().toString().startsWith("validation.")) {
                name = contraintes.getPropertyPath().toString().substring("validation.".length());
            }

            if (ex instanceof CommonConstraintViolationException){
                CommonConstraintViolationException cex = (CommonConstraintViolationException)ex;
                name = String.format("%s%s", cex.getPropertyPrefix(), name);
            }

            attribut.setName(name);
            attribut.getListMessages().add(contraintes.getMessage());
        }

        return result;
    }

    public static BadRequestParameterDto methodArgumentNotValidException(MethodArgumentNotValidException ex) {

        log.info(ex.getMessage(), ex);

        BadRequestParameterDto result = new BadRequestParameterDto();

        for (ObjectError error : ex.getBindingResult().getAllErrors()){

            FieldError fieldError = (FieldError)error;

            AttributDto attribut = new AttributDto();
            result.getListErrorMessages().add(attribut);
            attribut.setType(TypeAttribut.RESOURCE);
            attribut.setName(String.format("%s.%s", error.getObjectName(), fieldError.getField()));
            attribut.getListMessages().add(error.getDefaultMessage());
        }


        return result;
    }

    public static BadRequestParameterDto badRequestParameterException(BadRequestParameterException ex) {

        log.info(ex.getMessage(), ex);

        return ex.getBadRequestParameterResource();
    }

    public static BadResponseDto messageException(MessageService messageService, MessageException ex) {

        log.info(ex.getMessage(), ex);

        Locale locale = LocaleContextHolder.getLocale();

        return MiscUtils.messageExceptionToBadResponseDto(ex, messageService, locale);
    }
}
