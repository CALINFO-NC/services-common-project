package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class KafkaResponse implements Serializable {

    Integer status;
    private Map<String, String> headers = new HashMap<>();
    private String body;
}
