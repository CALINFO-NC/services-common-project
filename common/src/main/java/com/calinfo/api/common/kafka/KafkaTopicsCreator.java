package com.calinfo.api.common.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 *
 */
@Component
@ConditionalOnProperty("common.kafka.enabled")
public class KafkaTopicsCreator implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(KafkaTopicsCreator.class);

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final KafkaAdmin kafkaAdmin;
    private final KafkaTopicNameResolver kafkaTopicNameResolver;
    private final KafkaCreateTopicWrapper kafkaCreateTopicWrapper;
    private final KafkaProperties kafkaProperties;

    public KafkaTopicsCreator(
            RequestMappingHandlerMapping requestMappingHandlerMapping,
            KafkaAdmin kafkaAdmin,
            KafkaTopicNameResolver kafkaTopicNameResolver,
            KafkaCreateTopicWrapper kafkaCreateTopicWrapper,
            KafkaProperties kafkaProperties) {

        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.kafkaAdmin = kafkaAdmin;
        this.kafkaTopicNameResolver = kafkaTopicNameResolver;
        this.kafkaCreateTopicWrapper = kafkaCreateTopicWrapper;
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfig())) {

            Set<String> existingTopics;

            try {

                existingTopics = client.listTopics().names().get();

                logger.info("Existing kafka topics : {} ", existingTopics);

            } catch (ExecutionException e) {

                logger.error("Impossible de récupérer la liste des topics kafka existants", e);
                throw new KafkaCreateTopicsException();

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                logger.error("Impossible de récupérer la liste des topics kafka existants", e);
                throw new KafkaCreateTopicsException();
            }

            // Nouveau topics liés au méthodes annotées avec KafkaPublicMethodResult
            Set<NewTopic> newTopics = requestMappingHandlerMapping
                    .getHandlerMethods()
                    // Récupère tous les handlers des controllers de l'application context
                    .entrySet()
                    .stream()
                    // Récupération du nom du topic kafka
                    // Et Enregistrement du mapping entre le nom classe + méthode et topic kafka
                    .flatMap(entry -> kafkaTopicNameResolver.resolve(
                            entry.getKey().getPatternsCondition().getPatterns(),
                            entry.getValue().getBeanType().getName(),
                            entry.getValue().getMethod().getName(),
                            entry.getKey().getMethodsCondition().getMethods())
                            .stream())
                    // Si le topic n'existe pas
                    .filter(topicName -> !existingTopics.contains(topicName))
                    // Suppression des doublons
                    .distinct()
                    .map(topicName -> new NewTopic(topicName,
                            kafkaProperties.getPartitions(),
                            kafkaProperties.getReplicas()))
                    .collect(Collectors.toSet());

            this.kafkaCreateTopicWrapper.createTopics(client, newTopics);

        }
    }

}