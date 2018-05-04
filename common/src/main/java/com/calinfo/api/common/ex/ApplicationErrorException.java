package com.calinfo.api.common.ex;

import org.springframework.http.HttpStatus;

/**
 * Created by dalexis on 02/12/2017.
 */
public class ApplicationErrorException extends MessageStatusException{

    public ApplicationErrorException(Throwable e){
        super(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }
}
