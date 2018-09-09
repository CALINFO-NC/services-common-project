package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.kafka.KafkaTopic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class KafkaSubServiceImpl implements KafkaSubService {


    @Override
    @KafkaTopic("topicA")
    public void topicAWithException() {
        throw new MessageStatusException(HttpStatus.BAD_REQUEST, "bad");
    }

    @Override
    @KafkaTopic("topicA")
    public void topicAWithoutException() {

    }
}
