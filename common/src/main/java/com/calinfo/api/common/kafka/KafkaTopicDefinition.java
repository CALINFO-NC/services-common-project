package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class KafkaTopicDefinition {


    @Getter
    @Setter
    boolean prefixTopicNameWithApplicationId;

    @Getter
    @Setter
    boolean prefixTopicNameWithDomain;

    @Getter
    @Setter
    private String topicName;

    @Getter
    @Setter
    private String fullQualifiedServiceClassName;

    @Getter
    @Setter
    private String methodServiceName;

    @Getter
    @Setter
    private List<String> parametersType = new ArrayList<>();

    @Getter
    @Setter
    private String returnType;
}
