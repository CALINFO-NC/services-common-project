package com.calinfo.api.common.swagger;

import com.calinfo.api.common.kafka.KafkaTopicNameResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

/**
 * Created by dalexis on 08/06/2018.
 */
@Component
@Order(CollectorOperationBuilder.ORDER)
@ConditionalOnClass({ApiInfo.class})
@ConditionalOnProperty(value = "common.configuration.kafka-event.enabled", matchIfMissing = true)
public class DefaultNoteKafkaOperationBuilder implements OperationBuilderPlugin {

    @Autowired
    private SwaggerCollector collector;

    @Autowired
    private KafkaTopicNameResolver kafkaTopicNameResolver;

    @Override
    public void apply(OperationContext operationContext) {


        StringBuilder newNote = new StringBuilder();
        String note = operationContext.operationBuilder().build().getNotes();
        if (!StringUtils.isBlank(note)){
            newNote.append(note);
            newNote.append("<br/>");
        }

        boolean first = true;
        for (SwaggerItemCollector itemCollector : collector.getSwaggerItemByUri(operationContext.requestMappingPattern())){

            if (first) {
                newNote.append("<br/>Kafka topics :<ul>");
                first = false;
            }
            newNote.append("<li>");
            newNote.append(kafkaTopicNameResolver.getTopicName(itemCollector));
            newNote.append("</li>");
        }

        if (!first){
            newNote.append("</ul>");
        }


        operationContext.operationBuilder().notes(newNote.toString());
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
