package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.kafka.KafkaEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
@Profile("kafka")
public class Receiver {

    private static final long AWAIT = 7_000;

    private KafkaEvent kafkaEventTopic1 = null;
    private CountDownLatch latch1 = null;

    private KafkaEvent kafkaEventTopicA = null;
    private CountDownLatch latchA = null;

    @KafkaListener(topics = "topic1")
    public void receiveTopic1(KafkaEvent kafkaEvent) {
        latch1.countDown();
        this.kafkaEventTopic1 = kafkaEvent;
    }

    @KafkaListener(topics = "topicA")
    public void receiveTopicA(KafkaEvent kafkaEvent) {
        latchA.countDown();
        this.kafkaEventTopicA = kafkaEvent;
    }

    public KafkaEvent getKafkaEventTopic1() throws InterruptedException {
        latch1.await(AWAIT, TimeUnit.MILLISECONDS);
        return kafkaEventTopic1;
    }

    public KafkaEvent getKafkaEventTopicA() throws InterruptedException {
        latchA.await(AWAIT, TimeUnit.MILLISECONDS);
        return kafkaEventTopicA;
    }

    public void clearKafkaEvent(){
        latch1 = new CountDownLatch(1);
        kafkaEventTopic1 = null;
        latchA = new CountDownLatch(1);
        kafkaEventTopicA = null;
    }
}
