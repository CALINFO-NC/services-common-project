package com.calinfo.api.common.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@ConditionalOnProperty(value = "common.configuration.kafka-event.enabled")
@Configuration
public class KafkaThreadPoolsConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaThreadPoolsConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean(name = {"monoThreadPool"})
    public Executor monoThreadPool() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);

        executor.setQueueCapacity(kafkaProperties.getPoolCapacity());
        executor.setKeepAliveSeconds(kafkaProperties.getTimoutInSecond());
        executor.setThreadNamePrefix(kafkaProperties.getThreadNamePrefix());

        executor.initialize();

        return executor;
    }

}
