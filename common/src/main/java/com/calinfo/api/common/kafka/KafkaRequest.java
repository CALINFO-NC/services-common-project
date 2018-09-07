package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
class KafkaRequest implements Serializable {

    private String method;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String[]> parameters = new HashMap<>();
    private String body;
}
