package com.calinfo.api.common.swagger;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.OperationNameGenerator;



@Component
@Primary
@ConditionalOnProperty(value = "common.configuration.swagger.name-generator.enabled", matchIfMissing = true)
public class CommonOperationNameGenerator implements OperationNameGenerator {

    @Override
    public String startingWith(String prefix) {
        return prefix;
    }
}