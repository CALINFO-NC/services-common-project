package com.calinfo.api.common.io;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableAsync(mode = AdviceMode.ASPECTJ)
@ComponentScan(value = {"com.calinfo.api.common"})
public class MocksApplication {
    public static void main(String[] args) {
        SpringApplication.run(MocksApplication.class, args);
    }
}