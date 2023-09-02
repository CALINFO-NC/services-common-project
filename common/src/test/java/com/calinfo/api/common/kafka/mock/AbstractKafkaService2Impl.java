package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.kafka.KafkaTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public abstract class AbstractKafkaService2Impl<T> {

    @Autowired
    private KafkaSubService kafkaSubService;

    @KafkaTopic(value = "topic0", prefixTopicNameWithApplicationId = true, kafkaPrefixeMandatory = false)
    public void topic0WithPrefix(T param) {

        TestResource res = new TestResource();
        res.setProp1("123");
        kafkaSubService.topicAWithoutException("A12", res);
    }

}
