package com.calinfo.api.common.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;

@Configuration
@ConditionalOnProperty("common.kafka.enabled")
@EnableKafka
public class KafkaConfig {

    private final KafkaPubSubConfig kafkaProducerConfig;

    public KafkaConfig(KafkaPubSubConfig kafkaProducerConfig) {
        this.kafkaProducerConfig = kafkaProducerConfig;
    }

    @Bean
    public <T> KafkaTemplate<String, T> kafkaTemplate() {
        return new KafkaTemplate(producerFactory());
    }

    @Bean
    public <T> ProducerFactory<String, T> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProducerConfig.producerConfigs());
    }

    @Bean
    public <T> ConsumerFactory<String, T> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProducerConfig.consumerConfigs());
    }

}
