package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class KafkaEvent {

    private String topic;
    private String fullQualifiedServiceClassName;
    private String methodServiceName;

    private Set<KafkaObject> parameters = new HashSet<>();
    private KafkaObject result;

    private boolean resultException;
}
