package com.calinfo.api.common.ex;

import com.calinfo.api.common.MessageStructure;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
public class MessageException extends RuntimeException {

    private Map<String, List<MessageStructure>> mapErrorMessagesFields = new HashMap<>();

    private List<MessageStructure> listErrorMessages = new ArrayList<>();

    public MessageException(MessageStructure errorMsg, MessageStructure... errorMsgTab){
        getListErrorMessages().add(errorMsg);
        for (MessageStructure item : errorMsgTab)
            getListErrorMessages().add(item);
    }

    public MessageException(String fieldName, List<MessageStructure> fieldErrors){
        getMapErrorMessagesFields().put(fieldName, fieldErrors);
    }

    public MessageException(HashMap<String, List<MessageStructure>> fieldsErrors){

        for (Map.Entry<String, List<MessageStructure>> entry : fieldsErrors.entrySet()){
            getMapErrorMessagesFields().put(entry.getKey(), entry.getValue());
        }
    }

    public MessageException(List<MessageStructure> listErrorMessages, Map<String, List<MessageStructure>> mapErrorMessagesFields){
        this.listErrorMessages = listErrorMessages;
        this.mapErrorMessagesFields = mapErrorMessagesFields;
    }
}
