package com.calinfo.api.common.ex;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
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
        this(badRequestParameterResource, null);
    }

    public BadRequestParameterException(BadRequestParameterDto badRequestParameterResource, Throwable cause){
        super(constructMessage(badRequestParameterResource), cause);
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
