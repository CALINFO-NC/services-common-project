package com.calinfo.api.common.ex.handler;

import com.calinfo.api.common.MessageStructure;
import com.calinfo.api.common.dto.AttributDto;
import com.calinfo.api.common.ex.BadRequestParameterException;
import com.calinfo.api.common.ex.CommonConstraintViolationException;
import com.calinfo.api.common.ex.MessageException;
import com.calinfo.api.common.response.BadRequestParameterResponse;
import com.calinfo.api.common.response.BadResponseResponse;
import com.calinfo.api.common.service.MessageService;
import com.calinfo.api.common.type.TypeAttribut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.Serializable;
import java.util.*;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @Autowired
    private MessageService messageService;



    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BadRequestParameterResponse missingServletRequestParameterException(MissingServletRequestParameterException ex) {

        log.info(ex.getMessage(), ex);

        BadRequestParameterResponse result = new BadRequestParameterResponse();

        AttributDto attribut = new AttributDto();
        result.getListErrorMessages().add(attribut);
        attribut.setType(TypeAttribut.REQUEST);
        attribut.setName(ex.getParameterName());
        attribut.getListMessages().add(ex.getMessage());

        return result;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BadRequestParameterResponse constraintViolationException(ConstraintViolationException ex) {

        log.info(ex.getMessage(), ex);

        BadRequestParameterResponse result = new BadRequestParameterResponse();

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BadRequestParameterResponse methodArgumentNotValidException(MethodArgumentNotValidException ex) {

        log.info(ex.getMessage(), ex);

        BadRequestParameterResponse result = new BadRequestParameterResponse();

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

    @ExceptionHandler(BadRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BadRequestParameterResponse badRequestParameterException(BadRequestParameterException ex) {

        log.info(ex.getMessage(), ex);

        return ex.getBadRequestParameterResource();
    }

    @ExceptionHandler(MessageException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public BadResponseResponse messageException(HttpServletRequest request, MessageException ex) {

        log.info(ex.getMessage(), ex);

        String language = request.getHeader("language");
        Locale locale = Locale.ENGLISH;
        if (language != null){
            locale = new Locale(language);
        }


        BadResponseResponse result = new BadResponseResponse();
        for (MessageStructure ms: ex.getErrors().getGlobalErrors()){
            result.getListErrorMessages().add(messageService.translate(locale, ms.getMessageCode(), ms.getParameters().stream().toArray(size -> new Serializable[size])));
        }

        for (Map.Entry<String, List<MessageStructure>> entry : ex.getErrors().getFieldsErrors().entrySet()){
            List<MessageStructure> value = entry.getValue();
            List<String> lstMsg = new ArrayList<>();

            for (MessageStructure ms: value){
                String msg = messageService.translate(locale, ms.getMessageCode(), ms.getParameters().stream().toArray(size -> new Serializable[size]));
                lstMsg.add(msg);
            }

            result.getMapErrorMessagesFields().put(entry.getKey(), lstMsg);
        }

        return result;
    }

}