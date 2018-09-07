package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.kafka.KafkaEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class Receiver {

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }

    @KafkaListener(topics = "COMMON.default.v1.kakfa-test-controller.read", groupId = "boot.t")
    public void receive(KafkaEvent consumerRecord) {
        latch.countDown();
    }
}
