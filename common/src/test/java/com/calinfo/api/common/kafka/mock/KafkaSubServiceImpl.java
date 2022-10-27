package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.kafka.KafkaTopic;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class KafkaSubServiceImpl implements KafkaSubService {


    @Override
    @KafkaTopic(value = "topicA", prefixTopicNameWithApplicationId = false, prefixTopicNameWithDomain = false, kafkaPrefixeMandatory = false)
    public void topicAWithException() {
        throw new MessageStatusException(HttpStatus.BAD_REQUEST, "bad");
    }

    @Override
    @KafkaTopic(value = "topicB", prefixTopicNameWithApplicationId = false, prefixTopicNameWithDomain = false, kafkaPrefixeMandatory = false)
    public TestResource topicAWithoutException(String id, TestResource resource) {

        TestResource result = new TestResource();
        result.setProp1("Propppp");
        return result;
    }
}
