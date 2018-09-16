package com.calinfo.api.common.kafka;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@ConditionalOnProperty(value = "common.configuration.kafka-event.enabled")
@Configuration
public class KafkaThreadPoolsConfig {

    @Value("${common.configuration.kafka-event.sender.threadPool.corePoolSize:1}")
    private Integer corePoolSize;

    @Value("${common.configuration.kafka-event.sender.threadPool.maxPoolSize:5}")
    private Integer maxPoolSize;


    @Bean(name = {"monoThreadPool"})
    public Executor monoThreadPool() {

        val executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);

        executor.initialize();

        return executor;
    }


    @Bean(name = {"kafkaEventsSender"})
    public Executor kafkaEventsSender() {

        val executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);

        executor.initialize();

        return executor;
    }
}
