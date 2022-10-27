package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.kafka.KafkaTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private KafkaSubService kafkaSubService;

    @Override
    @KafkaTopic(value = "topic0", prefixTopicNameWithApplicationId = true, kafkaPrefixeMandatory = false)
    public void topic0WithPrefix() {

        TestResource res = new TestResource();
        res.setProp1("123");
        kafkaSubService.topicAWithoutException("A12", res);
    }

    @Override
    @KafkaTopic(value = "topic1", prefixTopicNameWithApplicationId = false, prefixTopicNameWithDomain = false, kafkaPrefixeMandatory = false)
    public void topic1WithoutExceptionAndWithoutExceptionTopicA() {

        TestResource res = new TestResource();
        res.setProp1("123");
        kafkaSubService.topicAWithoutException("A12", res);
    }

    @Override
    @KafkaTopic(value = "topic2", prefixTopicNameWithApplicationId = false, prefixTopicNameWithDomain = false, kafkaPrefixeMandatory = false)
    public void topic2WithoutExceptionAndWithExceptionTopicB() {
        kafkaSubService.topicAWithException();
    }

    @Override
    @KafkaTopic(value = "topic3", prefixTopicNameWithApplicationId = false, prefixTopicNameWithDomain = false, kafkaPrefixeMandatory = false)
    public void topic3WithExceptionBeforeCallTopicA() {

        launchException();

        TestResource res = new TestResource();
        res.setProp1("123");
        kafkaSubService.topicAWithoutException("A12", res);

    }

    @Override
    @KafkaTopic(value = "topic4", prefixTopicNameWithApplicationId = false, prefixTopicNameWithDomain = false, kafkaPrefixeMandatory = false)
    public void topic4WithExceptionAfterCallTopicA() {

        TestResource res = new TestResource();
        res.setProp1("123");
        kafkaSubService.topicAWithoutException("A12", res);
        launchException();
    }


    private void launchException(){
        throw new MessageStatusException(HttpStatus.FORBIDDEN, "msg");
    }
}
