package com.calinfo.api.common.kafka.mock;

public interface KafkaService {

    void topic0WithPrefix();
    void topic1WithoutExceptionAndWithoutExceptionTopicA();
    void topic2WithoutExceptionAndWithExceptionTopicB();
    void topic3WithExceptionBeforeCallTopicA();
    void topic4WithExceptionAfterCallTopicA();
}
