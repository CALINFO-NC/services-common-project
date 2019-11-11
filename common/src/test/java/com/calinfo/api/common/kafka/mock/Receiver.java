package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.kafka.KafkaEvent;
import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.ArrayList;
import java.util.List;

public class Receiver {

    @Getter
    private List<KafkaEvent> lstKafkaEvent = new ArrayList<>();

    @KafkaListener(topics = "topic1")
    public void receiveTopic1(KafkaEvent kafkaEvent) {
    }

    @KafkaListener(topics = "topic2")
    public void receiveTopic2(KafkaEvent kafkaEvent) {
        lstKafkaEvent.add(kafkaEvent);
    }

    @KafkaListener(topics = "topic3")
    public void receiveTopic3(KafkaEvent kafkaEvent) {
        lstKafkaEvent.add(kafkaEvent);
    }

    @KafkaListener(topics = "topic4")
    public void receiveTopic4(KafkaEvent kafkaEvent) {
        lstKafkaEvent.add(kafkaEvent);
    }

    @KafkaListener(topics = "topicA")
    public void receiveTopicA(KafkaEvent kafkaEvent) {
        lstKafkaEvent.add(kafkaEvent);
    }

    @KafkaListener(topics = "topicB")
    public void receiveTopicB(KafkaEvent kafkaEvent) {
        lstKafkaEvent.add(kafkaEvent);
    }

}
