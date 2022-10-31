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
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public static String deleteAnnotationToStringMirror(String typeClass){

        String[] spl = typeClass.split(" "); // On supprime les annotation
        return spl[spl.length - 1];
    }


    public static List<String> splitToClassFromStringMirrorClass(String typeClass){

        List<String> result = new ArrayList<>();

        if (!StringUtils.isBlank(typeClass)) {

            String[] spl = deleteAnnotationToStringMirror(typeClass).replace(">", "").split("<");
            result.add(spl[0].trim());
            if (spl.length > 1) {
                Arrays.stream(spl[1].split(",")).forEach(i -> result.addAll(splitToClassFromStringMirrorClass(i.trim())));
            }
        }

        return result;
    }

    public static List<String> splitToClassFromMetadataService(KafkaMetadataService metadataService){

        List<String> result = new ArrayList<>();

        splitToClassFromStringMirrorClass(metadataService.getReturnType()).forEach(result::add);
        metadataService.getParametersTypes().entrySet().stream().map(Map.Entry::getValue).forEach(i -> splitToClassFromStringMirrorClass(i).forEach(result::add));

        return result;
    }
}
