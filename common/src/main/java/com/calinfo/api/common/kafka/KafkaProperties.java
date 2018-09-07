package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@ConditionalOnProperty("common.configuration.kafka.enabled")
@ConfigurationProperties(prefix = "common.configuration.kafka")
@Configuration
@Getter
@Setter
class KafkaProperties {

    private int partitions = 1;

    private short replicas = 1;

}
