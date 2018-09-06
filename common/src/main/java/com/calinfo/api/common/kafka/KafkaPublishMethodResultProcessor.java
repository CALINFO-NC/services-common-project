package com.calinfo.api.common.kafka;

import com.calinfo.api.common.ex.MessageStatusException;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public KafkaPublishMethodResultProcessor(
            KafkaTemplate<String, Object> kafkaTemplate,
            KafkaTopicNameResolver kafkaTopicNameResolver) {

        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicNameResolver = kafkaTopicNameResolver;
    }


    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object publishToKafka(ProceedingJoinPoint joinPoint) throws Throwable {

        Class<?> clazz = ClassUtils.getUserClass(joinPoint.getThis());
        String topic = kafkaTopicNameResolver.getTopic(clazz.getName(), joinPoint.getSignature().getName());

        if (null == topic) {
            return joinPoint.proceed();
        }

        try {

            return sendToKafka(topic, joinPoint.getArgs(), joinPoint.proceed());

        } catch (MessageStatusException msg) {

            sendToKafka(topic, joinPoint.getArgs(), new KafkaErrorMessage(msg.getMessage(), msg.getStatus()));

            throw msg;

        }
    }

    private <T> T sendToKafka(String topic, Object[] args, T result) {

        KakfaRequestMessage kakfaRequestMessage = new KakfaRequestMessage();
        kakfaRequestMessage.setParams(args);
        kakfaRequestMessage.setResult(result);
        kakfaRequestMessage.setResultType(result.getClass().getName());

        kafkaTemplate.send(topic, kakfaRequestMessage);

        return result;
    }
}
