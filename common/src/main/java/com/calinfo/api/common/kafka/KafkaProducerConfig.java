package com.calinfo.api.common.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty("common.kafka.enabled")
public class KafkaProducerConfig implements IKafkaProducerConfig {

    @Value("${common.kafka.url}")
    private String kafkaUrl;

    public Map<String, Object> producerConfigs() {

        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return config;
    }
}
