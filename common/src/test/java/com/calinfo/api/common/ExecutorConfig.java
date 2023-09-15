package com.calinfo.api.common;

import com.calinfo.api.common.domain.DomainAwarePoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

@Profile("asyncConfig")
@Configuration
@EnableAsync
public class ExecutorConfig extends AsyncConfigurerSupport {

    @Override
    @Bean
    public Executor getAsyncExecutor() {
        return new DomainAwarePoolExecutor();
    }
}
