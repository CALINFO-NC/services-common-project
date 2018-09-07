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

    public static final String TOPIC =  "COMMON.default.v1.kakfa-test-controller.GET.read";

    private KafkaEvent kafkaEvent = null;

    private CountDownLatch latch = null;

    @KafkaListener(topics = Receiver.TOPIC)
    public void receive(KafkaEvent kafkaEvent) {
        latch.countDown();
        this.kafkaEvent = kafkaEvent;
    }

    public KafkaEvent getKafkaEvent() throws InterruptedException {
        latch.await(10000, TimeUnit.MILLISECONDS);
        return kafkaEvent;
    }

    public void clearKafkaEvent(){
        latch = new CountDownLatch(1);
        kafkaEvent = null;
    }
}
