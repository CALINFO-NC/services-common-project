package com.calinfo.api.common.kafka;

/**
 *
 */
public class CreateTopicEvent {

    private KafkaEvent kafkaEvent;

    private CreateTopicEvent() {}

    public KafkaEvent getKafkaEvent() {
        return kafkaEvent;
    }

    public static CreateTopicEvent of(
            KafkaEvent event) {

        CreateTopicEvent result = new CreateTopicEvent();

        result.kafkaEvent = event;

        return result;

    }
}
