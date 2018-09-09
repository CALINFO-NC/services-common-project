package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaObject {

    private String fullQualifiedClassName;
    private Object value;

    public <T> T get(){
        return (T)value;
    }
}
