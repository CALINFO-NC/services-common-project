package com.calinfo.api.common.kafka;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
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

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component // Ici on utilise pas @Configuration (voir https://stackoverflow.com/questions/53484529/inspection-info-verifies-configurationproperties-setup-new-in-2018-3-intellij)
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

    /**
     * false pour désactiver l'enveoie des évènements à KAFKA.
     * Passer se paramètre à false permet une gestion customisé de la recetion des évènements KAFKA
     */
    private boolean kafkaListenerEnabled = true;
}
