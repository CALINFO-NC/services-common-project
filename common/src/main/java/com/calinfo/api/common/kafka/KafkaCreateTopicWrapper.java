package com.calinfo.api.common.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ExecutionException;


/**
 * Cette classe existe pour permettre de "mocker" la création de topics.
 * <p>
 * Il y a une erreur sur la création de topics dans le EmbeddedKafka,
 * cela fonctionne sur un kafka normal par contre.
 * <p>
 * Ce bean est "mocké" dans les tests, c'est son utilité et l'explication de l'absence de couverture de tests dessus.
 */
@Component
@ConditionalOnProperty("common.kafka.enabled")
public class KafkaCreateTopicWrapper {

    private final Logger logger = LoggerFactory.getLogger(KafkaCreateTopicWrapper.class);

    public void createTopics(AdminClient client, Set<NewTopic> newTopics) {

        try {

            client.createTopics(newTopics).all().get();

            logger.info("New kafka topics : {} ", newTopics);

        } catch (ExecutionException e) {

            logger.error("Echec de la création des nouveaux topics kafka", e);

            throw new KafkaCreateTopicsException();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            logger.error("Echec de la création des nouveaux topics kafka", e);

            throw new KafkaCreateTopicsException();


        }

    }
}
