package com.calinfo.api.common.kafka;

import com.calinfo.api.common.resource.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@ConditionalOnProperty("common.kafka.enabled")
@EnableKafka
public class KafkaConfig {

    private final IKafkaProducerConfig kafkaProducerConfig;

    public KafkaConfig(IKafkaProducerConfig kafkaProducerConfig) {
        this.kafkaProducerConfig = kafkaProducerConfig;
    }

    @Bean
    public <T extends Resource> KafkaTemplate<String, T> kafkaTemplate() {
        return new KafkaTemplate(producerFactory());
    }

    @Bean
    public <T extends Resource> ProducerFactory<String, T> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProducerConfig.producerConfigs());
    }

}
