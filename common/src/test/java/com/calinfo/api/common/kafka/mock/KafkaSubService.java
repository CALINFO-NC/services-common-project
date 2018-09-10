package com.calinfo.api.common.kafka.mock;

public interface KafkaSubService {

    void topicAWithException();

    TestResource topicAWithoutException(String id, TestResource resource);
}
