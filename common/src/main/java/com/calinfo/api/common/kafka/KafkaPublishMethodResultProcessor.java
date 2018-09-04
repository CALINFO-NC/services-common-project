package com.calinfo.api.common.kafka;

import com.calinfo.api.common.resource.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;


/**
 *
 */
@Aspect
@Component
@ConditionalOnProperty("common.kafka.enabled")
class KafkaPublishMethodResultProcessor {

    private final Logger logger = LoggerFactory.getLogger(KafkaPublishMethodResultProcessor.class);

    private final KafkaTopicNameResolver kafkaTopicNameResolver;
    private final KafkaTemplate<String, Resource> kafkaTemplate;


    public KafkaPublishMethodResultProcessor(
            KafkaTemplate<String, Resource> kafkaTemplate,
            KafkaTopicNameResolver kafkaTopicNameResolver) {

        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicNameResolver = kafkaTopicNameResolver;
    }


    @Around("@annotation(KafkaPublishMethodResult)")
    public Object publishToKafka(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = joinPoint.proceed();

        if (!Resource.class.isAssignableFrom(result.getClass())) {

            logger.warn(
                    "La méthode {} de la classe {} ne peut pas être annotée avec CUDKaftaPublisher car son type de retour n'étend pas Resource"
                    , joinPoint.getSignature()
                    , joinPoint.getThis().getClass());

            return result;

        }

        Class<?> clazz = ClassUtils.getUserClass(joinPoint.getThis());

        kafkaTemplate.send(kafkaTopicNameResolver.getTopic(clazz.getName(), joinPoint.getSignature().getName()), (Resource) result);

        return result;
    }
}
