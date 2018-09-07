package com.calinfo.api.common.kafka;

import com.calinfo.api.common.swagger.SwaggerCollector;
import com.calinfo.api.common.swagger.SwaggerItemCollector;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 *
 */
@Component
@ConditionalOnProperty(value = "common.configuration.kafka-event.enabled", matchIfMissing = true)
public class KafkaApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(KafkaApplicationStartup.class);

    private static final int TOPIC_PARTITION = 1;
    private static final short TOPIC_REPLICA = 1;

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Autowired
    private KafkaTopicNameResolver kafkaTopicNameResolver;

    @Autowired
    private SwaggerCollector swaggerCollector;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfig())) {

            try {
                Set<String> existingTopics = client.listTopics().names().get();
                logger.info("Existing kafka topics : {} ", existingTopics);
                client.createTopics(getTopicList(existingTopics)).all().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new KafkaCreateTopicsException("Echec de la création des nouveaux topics kafka", e);
            }
        }
    }

    private Set<NewTopic> getTopicList(Set<String> existingTopics){

        Set<NewTopic> lstTopic = new HashSet<>();
        for (SwaggerItemCollector swaggerItemCollector : swaggerCollector.getAll()){
            String topicName = kafkaTopicNameResolver.getTopicName(swaggerItemCollector);

            if (!lstTopic.stream().anyMatch(nt -> nt.name().equals(topicName)) && !existingTopics.stream().anyMatch(n -> n.equals(topicName))){
                NewTopic newTopic = new NewTopic(topicName, TOPIC_PARTITION, TOPIC_REPLICA);
                lstTopic.add(newTopic);
            }
        }

        return lstTopic;
    }

}
