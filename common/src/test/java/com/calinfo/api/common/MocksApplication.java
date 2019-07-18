package com.calinfo.api.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableAsync(mode = AdviceMode.ASPECTJ)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MocksApplication {
    public static void main(String[] args) {
        SpringApplication.run(MocksApplication.class, args);
    }
}