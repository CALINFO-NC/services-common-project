package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.kafka.KafkaTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private KafkaSubService kafkaSubService;


    @Override
    @KafkaTopic(value = "topic1", prefixTopicNameWithApplicationName = false)
    public void topic1WithoutExceptionAndWithoutExceptionTopicA() {

        kafkaSubService.topicAWithoutException();
    }

    @Override
    @KafkaTopic(value = "topic2", prefixTopicNameWithApplicationName = false)
    public void topic2WithoutExceptionAndWithExceptionTopicB() {
        kafkaSubService.topicAWithException();
    }

    @Override
    @KafkaTopic(value = "topic3", prefixTopicNameWithApplicationName = false)
    public void topic3WithExceptionBeforeCallTopicA() {

        launchException();
        kafkaSubService.topicAWithoutException();

    }

    @Override
    @KafkaTopic(value = "topic4", prefixTopicNameWithApplicationName = false)
    public void topic4WithExceptionAfterCallTopicA() {

        kafkaSubService.topicAWithoutException();
        launchException();
    }


    private void launchException(){
        throw new MessageStatusException(HttpStatus.FORBIDDEN, "msg");
    }
}
