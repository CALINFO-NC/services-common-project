package com.calinfo.api.common.kafka;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2022 CALINFO
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

import com.calinfo.api.common.config.ApplicationProperties;
import com.calinfo.api.common.security.PrincipalManager;
import com.calinfo.api.common.tenant.DomainContext;
import com.calinfo.api.common.utils.DateUtils;
import com.calinfo.api.common.utils.ExceptionUtils;
import com.calinfo.api.common.utils.MiscUtils;
import com.calinfo.api.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.Principal;
import java.time.format.DateTimeFormatter;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(value = "common.configuration.kafka-event.enabled")
public class KafkaTopicAspect {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired(required = false)
    private PrincipalManager principalManager;

    @Around("execution(public * *(..)) && @annotation(com.calinfo.api.common.kafka.KafkaTopic)")
    public Object publishToKafka(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("'{}' intercepted", joinPoint.getSignature().getName());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        KafkaEvent kafkaEvent = new KafkaEvent();
        kafkaEvent.setUser(getKafkaUser());
        kafkaEvent.setApplication(getKafkaApplication());

        kafkaEvent.setDomain(null);
        if (DomainContext.isDomainInitialized()) {
            kafkaEvent.setDomain(DomainContext.getDomain());
        }

        KafkaTopic kafkaTopic = method.getAnnotation(KafkaTopic.class);
        KafkaTopicPrefix kafkaTopicPrefix = getKafkaPrefixTopic(joinPoint.getTarget().getClass());

        String topicName = MiscUtils.getTopicFullName(applicationProperties.getId(), DomainContext.getDomain(), kafkaTopic, kafkaTopicPrefix);
        String templateTopicName = MiscUtils.getTopicFullName(KafkaMetadataUtils.TEMPLATE_APPLICATION_ID, KafkaMetadataUtils.TEMPLATE_DOMAIN_NAME, kafkaTopic, kafkaTopicPrefix);

        KafkaMeasure mesure = new KafkaMeasure();
        kafkaEvent.setMeasure(mesure);

        kafkaEvent.setTopic(topicName);
        KafkaData data = new KafkaData();
        kafkaEvent.setData(data);

        KafkaMetadataTopic metadataTopic = KafkaMetadataUtils.getTopicMetadatas().get(templateTopicName);
        if (metadataTopic != null) {
            kafkaEvent.setMetadataService(metadataTopic.getMetadataService());
            kafkaEvent.setMetadataModels(KafkaMetadataUtils.getModelMetadatas(kafkaEvent.getMetadataService()));
        }

        Parameter[] parameters = method.getParameters();
        for (int index = 0; index < parameters.length; index++) {
            Object prmVal = joinPoint.getArgs()[index];
            data.getSerializedParametersValues().put(index, KafkaUtils.serialize(prmVal));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        mesure.setExecutionDate(DateUtils.now().format(formatter));
        long deb = System.currentTimeMillis();
        try {
            Object val = joinPoint.proceed();
            mesure.setExecutionDurationMillisecond(System.currentTimeMillis() - deb);
            data.setSerializedReturnValue(KafkaUtils.serialize(val));
            data.setReturnValueException(false);

            return val;
        } catch (Throwable e) {
            mesure.setExecutionDurationMillisecond(System.currentTimeMillis() - deb);

            data.setSerializedReturnValue(ExceptionUtils.getPrintValue(e));
            data.setReturnValueException(true);

            throw e;
        } finally {

            if (metadataTopic != null && (!kafkaTopic.kafkaPrefixeMandatory() || kafkaTopicPrefix != null)) {
                applicationEventPublisher.publishEvent(kafkaEvent);
            }

        }
    }

    private KafkaApplication getKafkaApplication(){

        KafkaApplication result = new KafkaApplication();
        result.setId(applicationProperties.getId());
        result.setName(applicationProperties.getName());
        result.setVersion(applicationProperties.getVersion());

        return result;
    }

    private KafkaTopicPrefix getKafkaPrefixTopic(Class<?> root){

        Class<?> clazz = root;
        while (clazz != null){

            KafkaTopicPrefix ano = clazz.getAnnotation(KafkaTopicPrefix.class);
            if (ano != null){
                return ano;
            }

            clazz = clazz.getSuperclass();
        }

        return null;
    }


    private KafkaUser getKafkaUser() {

        KafkaUser kafkaUser = null;

        if (principalManager == null) {
            return kafkaUser;
        }

        Principal principal = principalManager.getPrincipal();
        if (principal != null) {
            String login = principal.getName();

            kafkaUser = new KafkaUser();
            kafkaUser.setLogin(login);
            kafkaUser.setRoles(SecurityUtils.getRoleFormPrincipal(principal));
        }

        return kafkaUser;
    }
}
