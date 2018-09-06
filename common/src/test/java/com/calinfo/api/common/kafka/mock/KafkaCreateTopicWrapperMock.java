package com.calinfo.api.common.kafka.mock;


import com.calinfo.api.common.kafka.KafkaCreateTopicWrapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component(value = "KafkaCreateTopicWrapper")
@Primary
@EmbeddedKafka
@Profile("kafka")
public class KafkaCreateTopicWrapperMock extends KafkaCreateTopicWrapper {

    private final Logger logger = LoggerFactory.getLogger(KafkaCreateTopicWrapperMock.class);

    @Autowired(required = false)
    private KafkaEmbedded embeddedKafka;

    @Override
    public void createTopics(AdminClient client, Set<NewTopic> newTopics) {
        newTopics.stream().forEach(topic ->
                embeddedKafka.addTopics(topic.name())
        );
    }
}
