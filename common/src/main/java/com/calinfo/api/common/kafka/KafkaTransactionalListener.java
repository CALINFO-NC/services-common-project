package com.calinfo.api.common.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@ConditionalOnProperty(value = "common.configuration.kafka-event.enabled")
public class KafkaTransactionalListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaTransactionalListener.class);

    private static final int TOPIC_PARTITION = 1;
    private static final short TOPIC_REPLICA = 1;

    private final Set<String> existingTopics = new HashSet<>();

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Async("kafkaExecutor") // Thread poolé sur un unique evenement à la fois
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void call(KafkaEvent kafkaEvent) {

        log.info("Prepare topic '{}' to send", kafkaEvent.getTopic());

        String topicName = kafkaEvent.getTopic();

        if (existingTopics.contains(topicName)) {

            kafkaTemplate.send(topicName, kafkaEvent);

        } else {
            createTopicAndSendEvent(kafkaEvent);
        }
    }

    private KafkaFuture.Function<Set<String>, Object> createTopicIfNeededAndSendEvent(KafkaEvent kafkaEvent, AdminClient client) {
        return new KafkaFuture.Function<Set<String>, Object>() {

            @Override
            public Object apply(Set<String> topicNames) {

                String topicName = kafkaEvent.getTopic();


                    existingTopics.addAll(topicNames);

                    if (existingTopics.contains(topicName)) {

                        kafkaTemplate.send(topicName, kafkaEvent);

                    } else {


                        log.info("Register topic '{}' in Kafka", topicName);

                        existingTopics.add(topicName);

                        client.createTopics(Collections.singleton(new NewTopic(topicName, TOPIC_PARTITION, TOPIC_REPLICA))).all().thenApply(sendEventAfterTopicCreation(kafkaEvent));
                    }

                    return null;

            }
        };
    }


    private KafkaFuture.Function<Void, ListenableFuture<SendResult<String, Object>>> sendEventAfterTopicCreation(KafkaEvent kafkaEvent) {

        return new KafkaFuture.Function<Void, ListenableFuture<SendResult<String, Object>>>() {

            @Override
            public ListenableFuture<SendResult<String, Object>> apply(Void aVoid) {

                String topicName = kafkaEvent.getTopic();

                return kafkaTemplate.send(topicName, kafkaEvent);

            }
        };
    }

    private void createTopicAndSendEvent(KafkaEvent kafkaEvent){

        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfig())) {

            client.listTopics()
                    .names()
                    .thenApply(createTopicIfNeededAndSendEvent(kafkaEvent, client));

        }
    }

    @Bean
    @Qualifier("kafkaExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(1);
        threadPoolTaskExecutor.setQueueCapacity(kafkaProperties.getPoolCapacity());
        threadPoolTaskExecutor.setKeepAliveSeconds(kafkaProperties.getTimoutInSecond());
        threadPoolTaskExecutor.setThreadNamePrefix(kafkaProperties.getThreadNamePrefix());


        return threadPoolTaskExecutor;
    }

}
