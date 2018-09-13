package com.calinfo.api.common.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    private Set<String> existingTopics = new HashSet<>();

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void call(KafkaEvent kafkaEvent){

        log.info("Prepare topic '{}' to send", kafkaEvent.getTopic());

        try {

            String topicName = kafkaEvent.getTopic();
            if (!existingTopics.contains(topicName)){
                registerTopic(topicName);
            }

            kafkaTemplate.send(topicName, kafkaEvent);

        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    private void registerTopic(String topicName) throws ExecutionException, InterruptedException {

        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfig())) {

            Set<String> clientTopics = client.listTopics().names().get();

            if (existingTopics.isEmpty()) {
                existingTopics.addAll(clientTopics);
            }

            if (!existingTopics.contains(topicName)) {
                log.info("Register topic '{}' in Kafka", topicName);

                existingTopics.add(topicName);
                NewTopic newTopic = new NewTopic(topicName, TOPIC_PARTITION, TOPIC_REPLICA);
                // le .all.get sont la pour attendre la création du topic
                // FIXME Il faudrait le faire en asynchrone, avec dans le "then" ajouter la publication de l'évenement ?
                client.createTopics(Collections.singleton(newTopic)).all().get();
            }
        }
    }

}
