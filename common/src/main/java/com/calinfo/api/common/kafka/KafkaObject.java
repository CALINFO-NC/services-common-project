package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class KafkaObject {

    private String fullQualifiedClassName;
    private Object value;
}
