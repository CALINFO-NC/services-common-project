package com.calinfo.api.common.swagger;

import org.apache.commons.lang3.StringUtils;
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
public class DefaultNoteOperationBuilder implements OperationBuilderPlugin {

    @Autowired
    public SwaggerCollector collector;

    @Override
    public void apply(OperationContext operationContext) {


        StringBuilder newNote = new StringBuilder();
        String note = operationContext.operationBuilder().build().getNotes();
        if (!StringUtils.isBlank(note)){
            newNote.append(note);
            newNote.append("<br/>");
        }

        String sep = ".";
        boolean first = true;
        for (SwaggerItemCollector itemCollector : collector.getSwaggerItemByUri(operationContext.requestMappingPattern())){

            if (first) {
                newNote.append("<br/>");
                newNote.append(String.format("Kafka group : %s", itemCollector.getTitle().toLowerCase()));
                newNote.append("<ul>");
                first = false;
            }
            newNote.append("<li>");
            newNote.append("topic : common");
            newNote.append(sep);
            newNote.append(itemCollector.getVersion());
            newNote.append(sep);
            newNote.append(itemCollector.getGroup());
            newNote.append(sep);
            newNote.append(itemCollector.getHttpMethod().toString().toLowerCase());
            newNote.append(sep);
            newNote.append(itemCollector.getResourceName());
            newNote.append(sep);
            newNote.append(itemCollector.getOperationName());
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
