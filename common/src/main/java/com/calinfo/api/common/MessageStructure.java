package com.calinfo.api.common;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MessageStructure implements Serializable {

    private MessageCodeValue messageCode;

    private List<Serializable> parameters = new ArrayList<>();

    public MessageStructure(){

    }

    public MessageStructure(MessageCodeValue messageCode, Serializable... params){
        this.messageCode = messageCode;

        for (Serializable item: params)
            parameters.add(item);
    }
}
