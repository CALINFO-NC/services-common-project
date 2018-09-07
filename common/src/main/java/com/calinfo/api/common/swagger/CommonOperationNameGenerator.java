package com.calinfo.api.common.swagger;


import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.OperationNameGenerator;
import springfox.documentation.service.ApiInfo;


@Component
@Primary
@ConditionalOnClass({ApiInfo.class})
@ConditionalOnProperty(value = "common.configuration.swagger.name-generator.enabled", matchIfMissing = true)
public class CommonOperationNameGenerator implements OperationNameGenerator {

    @Override
    public String startingWith(String prefix) {
        return prefix;
    }
}