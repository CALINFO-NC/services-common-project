package com.calinfo.api.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(mode = AdviceMode.ASPECTJ) // TODO : Mettre ceci dans la doc, sinon nous avons un warning à l'execution des tâches Asynchrones
public class MocksApplication {
    public static void main(String[] args) {
        SpringApplication.run(MocksApplication.class, args);
    }
}