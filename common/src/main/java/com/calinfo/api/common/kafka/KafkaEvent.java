package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class KafkaEvent implements Serializable {

    private String topic;
    private KafkaRequest request;
    private KafkaResponse response;
}
