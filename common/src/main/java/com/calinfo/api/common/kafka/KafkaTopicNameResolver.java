package com.calinfo.api.common.kafka;

import com.calinfo.api.common.swagger.SwaggerItemCollector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty("common.configuration.kafka.enabled")
@Component
public class KafkaTopicNameResolver {


    public String getTopicName(SwaggerItemCollector item) {
        return item.getTitle() + "." + item.getGroup() + "." + item.getVersion() + "." + item.getResourceName() + "." + item.getOperationName();
    }

}
