package com.calinfo.api.common.kafka;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Devrait être placée sur un controller,
 * methods : renvoi la liste des méthodes nécessitant de créer des topics.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KakfaTopicInitializer {

    /**
     * La présence de cette fonction est due "l'impossibilité" de récupérer les annotations sur les méthodes instrumentés :
     *
     * L'idée de base était que quand une classe déclare l'annotation {@link KakfaTopicInitializer}, on parcoure ses méthodes et on sélectionne
     * les méthodes décorées de l'annotation @{@link KafkaPublishMethodResult}. Cependant les méthodes décorées de {@link KafkaPublishMethodResult}
     * sont instrumentées par aspect4j qui ne conserve pas les annotations sur le proxy de la méthode qu'il crée, ouch ?
     *
     *
     * @return la liste des méthodes a utiliser pour créer les topics kafka.
     */
    String[] methods();
}
