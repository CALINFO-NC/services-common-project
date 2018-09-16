package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "common.configuration.kafka-event")
@Getter
@Setter
public class KafkaProperties {

    /**
     * Activation de la gestion Kafka par le common
     */
    private boolean enabled = false;

    /**
     * Taille maximale de la file d'attente de message pour la sauvegarde des threads.
     */
    private int poolCapacity = 1000;

    /**
     * Si l'exécution dure plus longtemps que X secondes dans le pool, le traitement de la requête expirera et passera à
     * la requête suivante
     */
    private int timoutInSecond = 10;

    /**
     * Préfix des thread Kafka
     */
    private String threadNamePrefix = "common-kafka-";


    private Integer corePoolSize = 1;


    private Integer maxPoolSize = 5;
}
