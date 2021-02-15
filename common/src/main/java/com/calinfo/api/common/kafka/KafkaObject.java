package com.calinfo.api.common.kafka;

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

import com.calinfo.api.common.utils.MiscUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;


public class KafkaObject {

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private String fullQualifiedClassName;

    @Setter(AccessLevel.PACKAGE)
    @Getter
    String strValue;

    private Object cache = null;

    public <T> T get(){
        return get(fullQualifiedClassName);
    }

    public <T> T get(String fullQualifiedClassName){

        if (strValue == null){
            return null;
        }

        if (cache == null) {
            try {
                ObjectMapper objectMapper = MiscUtils.getObjectMapper();
                Class<?> clazz = Class.forName(fullQualifiedClassName);
                cache = objectMapper.readValue(strValue, clazz);
            } catch (ClassNotFoundException | IOException e) {
                throw new KafkaRestitutionException(fullQualifiedClassName, e);
            }
        }

        return (T)cache;
    }

    void set(Object object) throws JsonProcessingException {

        if (object == null){
            strValue = null;
        }
        else {
            ObjectMapper objectMapper = MiscUtils.getObjectMapper();
            strValue = objectMapper.writeValueAsString(object);
        }
        cache = null;
    }
}
