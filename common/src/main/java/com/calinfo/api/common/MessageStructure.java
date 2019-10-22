package com.calinfo.api.common;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 CALINFO
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
