package com.calinfo.api.common.kafka;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Configuration
@ConditionalOnProperty(value = "common.configuration.kafka-event.enabled", matchIfMissing = true)
public class KafkaTopicAspect {

    private static final Logger log = LoggerFactory.getLogger(KafkaTopicAspect.class);

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Around("execution(public * *(..)) && @annotation(com.calinfo.api.common.kafka.KafkaTopic)")
    public Object publishToKafka(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("'{}' intercepted", joinPoint.getSignature().getName());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        KafkaEvent kafkaEvent = new KafkaEvent();

        KafkaTopic kafkaTopic = method.getAnnotation(KafkaTopic.class);
        kafkaEvent.setTopic(kafkaTopic.value());
        kafkaEvent.setTopic(kafkaTopic.value());
        kafkaEvent.setFullQualifiedServiceClassName(method.getDeclaringClass().getName());
        kafkaEvent.setMethodServiceName(method.getName());

        Parameter[] parameters = method.getParameters();
        for (int index = 0; index < parameters.length; index++){
            Parameter parameter = parameters[index];
            KafkaObject kafkaParameter = new KafkaObject();
            kafkaEvent.getParameters().add(kafkaParameter);
            kafkaParameter.setFullQualifiedClassName(parameter.getType().getName());
            kafkaParameter.setValue(joinPoint.getArgs()[index]);
        }

        try {
            Object val = joinPoint.proceed();

            KafkaObject result = new KafkaObject();
            kafkaEvent.setResult(result);
            result.setFullQualifiedClassName(method.getReturnType().getName());
            kafkaEvent.setResultException(false);
            result.setValue(val);

            return val;
        }
        catch(Throwable e){

            KafkaObject result = new KafkaObject();
            kafkaEvent.setResult(result);
            result.setFullQualifiedClassName(method.getReturnType().getName());
            kafkaEvent.setResultException(true);

            // TODO : Mettre le resultat de l'exception dans result.setValue

            throw e;
        }finally {

            applicationEventPublisher.publishEvent(kafkaEvent);

        }
    }
}
