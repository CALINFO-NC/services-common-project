package com.calinfo.api.common.kafka;

import com.calinfo.api.common.swagger.SwaggerCollector;
import com.calinfo.api.common.swagger.SwaggerItemCollector;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 *
 */
//@Component
//@ConditionalOnProperty("common.configuration.kafka.enabled")
public class KafkaTopicsCreator implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(KafkaTopicsCreator.class);

    private final KafkaAdmin kafkaAdmin;
    private final KafkaTopicNameResolver kafkaTopicNameResolver;
    private final KafkaCreateTopicWrapper kafkaCreateTopicWrapper;
    private final KafkaProperties kafkaProperties;
    private final SwaggerCollector swaggerCollector;

    public KafkaTopicsCreator(
            KafkaAdmin kafkaAdmin,
            KafkaTopicNameResolver kafkaTopicNameResolver,
            KafkaCreateTopicWrapper kafkaCreateTopicWrapper,
            KafkaProperties kafkaProperties,
            SwaggerCollector swaggerCollector) {

        this.kafkaAdmin = kafkaAdmin;
        this.kafkaTopicNameResolver = kafkaTopicNameResolver;
        this.kafkaCreateTopicWrapper = kafkaCreateTopicWrapper;
        this.kafkaProperties = kafkaProperties;
        this.swaggerCollector = swaggerCollector;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfig())) {

            Set<String> existingTopics;

            try {
                existingTopics = client.listTopics().names().get();
                logger.info("Existing kafka topics : {} ", existingTopics);
            } catch (InterruptedException | ExecutionException e) {
                throw new KafkaCreateTopicsException("Impossible de récupérer la liste des topics kafka existants", e);
            }


            this.kafkaCreateTopicWrapper.createTopics(client, getTopicList(existingTopics));
        }
    }

    private Set<NewTopic> getTopicList(Set<String> existingTopics){

        Set<NewTopic> lstTopic = new HashSet<>();
        for (SwaggerItemCollector swaggerItemCollector : swaggerCollector.getAll()){
            String topicName = kafkaTopicNameResolver.getTopicName(swaggerItemCollector);

            if (!lstTopic.stream().anyMatch(nt -> nt.name().equals(topicName)) && !existingTopics.stream().anyMatch(n -> n.equals(topicName))){
                NewTopic newTopic = new NewTopic(topicName, kafkaProperties.getPartitions(), kafkaProperties.getReplicas());
                lstTopic.add(newTopic);
            }

        }

        return lstTopic;
    }

}
