package com.calinfo.api.common.kafka;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2020 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
@ConditionalOnExpression("${common.configuration.kafka-event.enabled:false} and ${common.configuration.kafka-event.kafka-listener-enabled:true}")
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

    @Async("kafkaMonoThreadPool")
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
