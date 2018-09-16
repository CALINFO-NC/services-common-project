package com.calinfo.api.common.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;


@Component
@ConditionalOnProperty(value = "common.configuration.kafka-event.enabled")
public class KafkaTransactionalListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaTransactionalListener.class);

    private static final int TOPIC_PARTITION = 1;
    private static final short TOPIC_REPLICA = 1;

    private final Set<String> existingTopics = new HashSet<>();

    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaAdmin kafkaAdmin;

    public KafkaTransactionalListener(
            final ApplicationEventPublisher applicationEventPublisher,
            final KafkaTemplate<String, Object> kafkaTemplate,
            final KafkaAdmin kafkaAdmin
    ) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.kafkaAdmin = kafkaAdmin;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async("monoThreadPool")
    @EventListener
    public void createTopicEvent(
            final CreateTopicEvent createTopicEvent) throws ExecutionException, InterruptedException {

        final KafkaEvent kafkaEvent = createTopicEvent.getKafkaEvent();

        String topicName = kafkaEvent.getTopic();

        log.info("Create topic (if needed) '{}' to send", topicName);

        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfig())) {

            Set<String> topics = client.listTopics()
                    .names()
                    .get(); // On bloque le thread dédié à cette tache, c'est ok ici sur la création de topic, le thread sert à ça.

            existingTopics.addAll(topics);

            if (!existingTopics.contains(topicName)) {

                log.info("Asking kafka to create topic '{}' to send", topicName);

                client.createTopics(
                        Collections.singleton(
                                new NewTopic(topicName, TOPIC_PARTITION, TOPIC_REPLICA))
                ).all().get(); // On attend que la création soit réalisée

            }

            kafkaTemplate.send(topicName, kafkaEvent);

        }

    }


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void call(KafkaEvent kafkaEvent) {

        log.info("Prepare topic '{}' to send", kafkaEvent.getTopic());

        String topicName = kafkaEvent.getTopic();

        if (existingTopics.contains(topicName)) {


            kafkaTemplate.send(topicName, kafkaEvent);

        } else {

            this.applicationEventPublisher.publishEvent(
                    CreateTopicEvent.of(kafkaEvent));
        }
    }

}
