package com.calinfo.api.common.kafka.mock;

public interface KafkaService {

    void topic1WithoutExceptionAndWithoutExceptionTopicA();
    void topic1WithoutExceptionAndWithExceptionTopicA();
    void topic1WithExceptionBeforeCallTopicA();
    void topic1WithExceptionAfterCallTopicA();
}
