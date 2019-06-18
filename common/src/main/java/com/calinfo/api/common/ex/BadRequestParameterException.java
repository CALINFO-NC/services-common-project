package com.calinfo.api.common.ex;

import com.calinfo.api.common.dto.AttributDto;
import com.calinfo.api.common.dto.BadRequestParameterDto;
import lombok.Getter;

/**
 * Created by dalexis on 02/12/2017.
 */
public class BadRequestParameterException extends RuntimeException{

    @Getter
    private final BadRequestParameterDto badRequestParameterResource;

    public BadRequestParameterException(BadRequestParameterDto badRequestParameterResource){
        super(constructMessage(badRequestParameterResource));
        this.badRequestParameterResource = badRequestParameterResource;
    }


    private static String constructMessage(BadRequestParameterDto badRequestParameterResource){

        StringBuilder msg = new StringBuilder();

        for (AttributDto attributDto : badRequestParameterResource.getListErrorMessages()){
            msg.append("(");
            msg.append(attributDto.getName());
            msg.append(",");
            msg.append(attributDto.getType());
            for (String str: attributDto.getListMessages()){
                msg.append(",");
                msg.append(str);
            }
            msg.append(")");
        }


        return msg.toString();
    }
}