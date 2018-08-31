package com.calinfo.api.common.kafka;

import com.calinfo.api.common.ex.MessageStatusException;
import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
public class TopicsControllers {

    private final Logger logger = LoggerFactory.getLogger(TopicsControllers.class);

    private KafkaAdmin kafkaAdmin;

    public TopicsControllers(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @GetMapping(value = "api/v1/topics")
    public Set<String> listTopics() {

        try {
            return AdminClient.create(kafkaAdmin.getConfig()).listTopics().names().get();

        } catch (InterruptedException | ExecutionException e) {

            logger.error("La récupération de la liste des topics a échouées", e);

            throw new MessageStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Le service est indisponible");
        }

    }
}
