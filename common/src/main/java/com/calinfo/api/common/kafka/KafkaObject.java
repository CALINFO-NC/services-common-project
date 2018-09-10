package com.calinfo.api.common.kafka;

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

        if (strValue == null){
            return null;
        }

        if (cache == null) {
            try {
                ObjectMapper objectMapper = MiscUtils.getObjectMapper();
                Class<?> clazz = Class.forName(fullQualifiedClassName);
                cache = objectMapper.readValue(strValue, clazz);
            } catch (ClassNotFoundException | IOException e) {
                throw new KafkaException(fullQualifiedClassName, e, false);
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
