package com.calinfo.api.common.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class KafkaResponse implements Serializable {

    Integer status;
    private Map<String, String> headers = new HashMap<>();
    private String body;
}
