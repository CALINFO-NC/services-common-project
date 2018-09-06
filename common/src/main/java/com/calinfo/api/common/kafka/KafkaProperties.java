package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "common.kafka.topics")
@Configuration
@Getter
@Setter
class KafkaProperties {


    private Integer partitions = 1;

    private Short replicas = 1;

}
