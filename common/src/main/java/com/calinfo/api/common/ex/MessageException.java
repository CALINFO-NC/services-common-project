package com.calinfo.api.common.ex;

import com.calinfo.api.common.ServiceErrorStructure;
import lombok.Getter;


@Getter
public class MessageException extends RuntimeException {

    private final ServiceErrorStructure errors;

    public MessageException(ServiceErrorStructure errors){
        this.errors = errors;
    }

}
