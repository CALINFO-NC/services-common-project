package com.calinfo.api.common;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MessageStructure {

    private MessageCodeValue messageCode;

    private List<Object> parameters = new ArrayList<>();

    public MessageStructure(){

    }

    public MessageStructure(MessageCodeValue messageCode, Object... params){
        this.messageCode = messageCode;

        for (Object item: params)
            parameters.add(item);
    }
}
