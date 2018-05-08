package com.calinfo.api.common.ex;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Created by dalexis on 02/12/2017.
 */
public class MessageStatusException extends RuntimeException{

    @Getter
    private final HttpStatus status;

    public MessageStatusException(HttpStatus statuts, String message){
        super(constructMessage(statuts, message));
        this.status = statuts;
    }

    protected MessageStatusException(HttpStatus statuts, String message, Throwable e){
        super(constructMessage(statuts, message), e);
        this.status = statuts;
    }

    private static String constructMessage(HttpStatus statuts, String message){

        StringBuilder msg = new StringBuilder();
        msg.append("Statut : ");
        msg.append(statuts.name());
        msg.append("(");
        msg.append(statuts.value());
        msg.append(")");

        if (message != null){
            msg.append(" : ");
            msg.append(message);
        }


        return msg.toString();
    }
}
