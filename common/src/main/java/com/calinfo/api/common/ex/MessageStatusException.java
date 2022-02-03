package com.calinfo.api.common.ex;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2022 CALINFO
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
