package com.calinfo.api.common.kafka;

import com.calinfo.api.common.ex.MessageStatusException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


/**
 * Les topics crées sont exposés sur un controller,
 * <p>
 * TODO Comment sécuriser l'url, voire si c'est pas mieux de créer une classe à côté.
 */
@Component
public class KafkaTopicInitializerAnnotationPostProcessor implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(KafkaTopicInitializerAnnotationPostProcessor.class);

    @Value("${spring.application.name}")
    private String appName;

    @Value("${common.kafka.url}")
    private String kafkaUrl;

    private KafkaAdmin kafkaAdmin;


    public KafkaTopicInitializerAnnotationPostProcessor(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }


    private Class<?> getUserClass(Object bean) {

        Class<?> clazz = bean.getClass();

        if (ClassUtils.isCglibProxy(bean)) {
            clazz = ClassUtils.getUserClass(bean);
        }

        return clazz;
    }

    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (kafkaUrl == null || kafkaUrl.isEmpty()) {
            return bean;
        }

        final Class<?> clazz = getUserClass(bean);

        final KakfaTopicInitializer[] kakfaTopicInitializers = clazz.getAnnotationsByType(KakfaTopicInitializer.class);

        if (kakfaTopicInitializers.length == 0) {
            return bean;
        }

        AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfig());

        Set<String> topics;

        try {

            topics = adminClient.listTopics().names().get();


            Set<NewTopic> topicsToCreate = Arrays.asList(kakfaTopicInitializers[0].methods())
                    .stream()
                    .map(method -> KakfaUtils.topicName(appName, clazz, method)) // Créer le nom du topic kafka
                    .filter(topicName -> !topics.contains(topicName)) // Exlcusion des topics existants dans kafka
                    .map(topicName -> new NewTopic(topicName, 1, (short) 1)) // Création des nouveaux topic
                    .collect(Collectors.toSet());

            if (!topicsToCreate.isEmpty()) {
                adminClient.createTopics(topicsToCreate).all().get();
                // Attente de la création des topics
                adminClient.close();
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new BeanInitializationException("Impossible de récupérér la liste des topics kafka, l'initialisation est impossible", e);
        }


        return bean;
    }

}

