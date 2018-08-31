package com.calinfo.api.common.kafka;

import com.calinfo.api.common.resource.Resource;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private static KafkaEmbedded embeddedKafka = new KafkaEmbedded(2, false);

    static {
        try {
            embeddedKafka.before();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static KafkaEmbedded getEmbeddedKafka() {
        return embeddedKafka;
    }

    @Bean
    public <T extends Resource> ProducerFactory<String, T> producerFactory() {

        Map<String, Object> config = KafkaTestUtils.producerProps(embeddedKafka);

        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<String, T>(config);
    }

    @Bean
    public KafkaAdmin admin() {
        return new KafkaAdmin(KafkaTestUtils.producerProps(embeddedKafka));
    }
}
