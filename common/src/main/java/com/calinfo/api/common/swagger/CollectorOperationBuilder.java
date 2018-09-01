package com.calinfo.api.common.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

/**
 * Created by dalexis on 08/06/2018.
 */
@Component
@Order(CollectorOperationBuilder.ORDER)
public class CollectorOperationBuilder implements OperationBuilderPlugin {

    public static final int ORDER = DefaultNicknameOperationBuilder.ORDER + 1;

    @Autowired
    private SwaggerCollector collector;

    @Override
    public void apply(OperationContext operationContext) {

        for (String tag : operationContext.operationBuilder().build().getTags()) {
            SwaggerItemCollector itemCollector = new SwaggerItemCollector();
            itemCollector.setOperationName(operationContext.getName());
            itemCollector.setResourceName(tag);
            itemCollector.setUri(operationContext.requestMappingPattern());
            itemCollector.setGroup(operationContext.getDocumentationContext().getGroupName());
            itemCollector.setHttpMethod(operationContext.httpMethod());
            itemCollector.setVersion(operationContext.getDocumentationContext().getApiInfo().getVersion());
            itemCollector.setTitle(operationContext.getDocumentationContext().getApiInfo().getTitle());
            collector.add(itemCollector);
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
