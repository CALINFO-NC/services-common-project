package com.calinfo.api.common.kafka;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Une méthode annotée verra le résultat de son exécution publiée dans kafka sur le topic :
 *
 * Le résultat de la méthode
 * Ne peut être utilisée que sur des controllers ?
 *
 * nomapp.nomclass.method
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface KafkaPublishMethodResult {

}
