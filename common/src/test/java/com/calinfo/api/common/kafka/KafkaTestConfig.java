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
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EmbeddedKafka
@Primary
@EnableKafka
@Profile("kafka")
public class KafkaTestConfig {

    @Autowired(required = false)
    private KafkaEmbedded kafkaEmbedded;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(KafkaTestUtils.producerProps(kafkaEmbedded));
    }

}
