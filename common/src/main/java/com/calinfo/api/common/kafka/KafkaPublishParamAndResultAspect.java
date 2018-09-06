package com.calinfo.api.common.kafka;

import com.calinfo.api.common.ex.MessageStatusException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.type.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@Aspect
@Component
@ConditionalOnProperty("common.kafka.enabled")
class KafkaPublishParamAndResultAspect {


    private final Logger logger = LoggerFactory.getLogger(KafkaPublishParamAndResultAspect.class);

    private final KafkaTopicNameResolver kafkaTopicNameResolver;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public KafkaPublishParamAndResultAspect(
            KafkaTemplate<String, Object> kafkaTemplate,
            KafkaTopicNameResolver kafkaTopicNameResolver) {

        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicNameResolver = kafkaTopicNameResolver;
    }


    @Around("@within(org.springframework.web.bind.annotation.RestController) && !@annotation(com.calinfo.api.common.kafka.KafkaIgnore)")
    public Object publishToKafka(ProceedingJoinPoint joinPoint) throws Throwable {

        Class<?> clazz = ClassUtils.getUserClass(joinPoint.getThis());
        String topic = kafkaTopicNameResolver.getTopic(clazz.getName(), joinPoint.getSignature().getName());

        if (null == topic) {
            return joinPoint.proceed();
        }


        try {

            return sendToKafka(topic, getParameters(joinPoint), joinPoint.proceed());

        } catch (SerializationException se) {

            logger.error("Impossible de sérialiser le paramètre, pour ignorer la méthode, ajouter l'annotation @KafkaIgnore sur la méthode");
            throw se;

        } catch (MessageStatusException msg) {

            sendToKafka(topic, joinPoint.getArgs(), new KafkaErrorMessage(msg.getMessage(), msg.getStatus()));

            throw msg;

        }
    }


    /**
     * Exclusions des paramètres annotés avec KafkaIgnore
     *
     * @param joinPoint
     * @return
     */
    private Object[] getParameters(ProceedingJoinPoint joinPoint) {

        List<Object> kafkaArgs = new ArrayList<>();


        Parameter[] parameters = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameters();

        for (int i = 0; i < joinPoint.getArgs().length; i++) {

            if (null == parameters[i].getAnnotation(KafkaIgnore.class)) {
                kafkaArgs.add(joinPoint.getArgs()[i]);
            }
        }

        return kafkaArgs.toArray();
    }


    private <T> T sendToKafka(String topic, Object[] args, T result) {

        KakfaRequestMessage kakfaRequestMessage = new KakfaRequestMessage();
        kakfaRequestMessage.setParams(args);
        kakfaRequestMessage.setResult(result);
        if (null != result) {
            kakfaRequestMessage.setResultType(result.getClass().getName());
        }

        kafkaTemplate.send(topic, kakfaRequestMessage);

        return result;
    }
}
