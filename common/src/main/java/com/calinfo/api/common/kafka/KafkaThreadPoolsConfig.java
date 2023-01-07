package com.calinfo.api.common.kafka;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2023 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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

    @Bean(name = {"kafkaMonoThreadPool"})
    public Executor getKafkaMonoThreadPool() {

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
