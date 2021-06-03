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

import com.calinfo.api.common.config.ApplicationProperties;
import com.calinfo.api.common.security.AbstractCommonPrincipal;
import com.calinfo.api.common.security.PrincipalManager;
import com.calinfo.api.common.security.SecurityProperties;
import com.calinfo.api.common.tenant.DomainContext;
import com.calinfo.api.common.utils.ExceptionUtils;
import com.calinfo.api.common.utils.MiscUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Collectors;


@Aspect
@Component
@ConditionalOnProperty(value = "common.configuration.kafka-event.enabled")
public class KafkaTopicAspect {

    private static final Logger log = LoggerFactory.getLogger(KafkaTopicAspect.class);

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired(required = false)
    private PrincipalManager principalManager;

    @Autowired(required = false)
    private SecurityProperties securityProperties;

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

        String topicName = MiscUtils.getTopicFullName(applicationProperties.getId(), kafkaTopic);

        kafkaEvent.setTopic(topicName);
        kafkaEvent.setFullQualifiedServiceClassName(method.getDeclaringClass().getName());
        kafkaEvent.setMethodServiceName(method.getName());

        Parameter[] parameters = method.getParameters();
        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            KafkaObject kafkaParameter = new KafkaObject();
            kafkaEvent.getParameters().put(index, kafkaParameter);
            kafkaParameter.setFullQualifiedClassName(parameter.getType().getName());
            kafkaParameter.set(joinPoint.getArgs()[index]);
        }

        try {
            Object val = joinPoint.proceed();

            KafkaObject result = new KafkaObject();
            kafkaEvent.setResult(result);
            result.setFullQualifiedClassName(method.getReturnType().getName());
            kafkaEvent.setResultException(false);
            result.set(val);

            return val;
        } catch (Throwable e) {

            KafkaObject result = new KafkaObject();
            kafkaEvent.setResult(result);
            result.set(ExceptionUtils.getPrintValue(e));
            result.setFullQualifiedClassName(e.getClass().getName());
            kafkaEvent.setResultException(true);

            throw e;
        } finally {

            applicationEventPublisher.publishEvent(kafkaEvent);

        }
    }

    private KafkaApplication getKafkaApplication(){

        KafkaApplication result = new KafkaApplication();
        result.setId(applicationProperties.getId());
        result.setName(applicationProperties.getName());
        result.setVersion(applicationProperties.getVersion());

        return result;
    }

    private KafkaUser getKafkaUser() {

        KafkaUser kafkaUser = null;

        if (principalManager == null) {
            return kafkaUser;
        }

        AbstractCommonPrincipal principal = principalManager.getPrincipal();
        if (principal != null) {
            String login = principal.getUsername();

            kafkaUser = new KafkaUser();
            kafkaUser.setLogin(login);
            kafkaUser.setRoles(principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

            kafkaUser.setSystemUser(false);
            if (securityProperties.getSystemLogin().equals(login)) {
                kafkaUser.setSystemUser(true);
            }

            kafkaUser.setAnonymousUser(false);
            if (securityProperties.getAnonymousLogin().equals(login)) {
                kafkaUser.setAnonymousUser(true);
            }
        }

        return kafkaUser;
    }
}
