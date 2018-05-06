package com.calinfo.api.common.tenant;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class DefaultDatasourceConditional implements Condition {


    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String urlJdbc = conditionContext.getEnvironment().getProperty("spring.datasource.url");
        return (urlJdbc != null);
    }
}