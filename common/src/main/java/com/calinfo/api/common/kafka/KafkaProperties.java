package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class KafkaProperties {

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
}
