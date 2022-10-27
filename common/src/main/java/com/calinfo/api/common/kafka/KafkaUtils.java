package com.calinfo.api.common.kafka;

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

import com.calinfo.api.common.utils.MiscUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaUtils {


    public static <T> T unserialize(Class<T> clazz, String strValue) throws IOException {

        if (strValue == null){
            return null;
        }

        ObjectMapper objectMapper = MiscUtils.getObjectMapper();
        return objectMapper.readValue(strValue, clazz);
    }

    public static String serialize(Object object) throws IOException {

        if (object == null){
            return null;
        }

        ObjectMapper objectMapper = MiscUtils.getObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
