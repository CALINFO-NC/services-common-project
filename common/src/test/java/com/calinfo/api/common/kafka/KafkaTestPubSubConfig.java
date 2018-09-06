package com.calinfo.api.common.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Primary
@EmbeddedKafka
@Profile("kafka")
@Component
public class KafkaTestPubSubConfig implements KafkaPubSubConfig {

    @Autowired
    private KafkaEmbedded kafkaEmbedded;

    public Map<String, Object> producerConfigs() {

        Map<String, Object> config = KafkaTestUtils.producerProps(kafkaEmbedded);

        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return config;
    }

    @Override
    public Map<String, Object> consumerConfigs() {

        Map<String, Object> config = KafkaTestUtils.consumerProps("testT", "false", kafkaEmbedded);

        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return config;
    }
}
