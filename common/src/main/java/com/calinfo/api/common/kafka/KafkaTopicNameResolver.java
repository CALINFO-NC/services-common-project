package com.calinfo.api.common.kafka;

import com.calinfo.api.common.swagger.SwaggerItemCollector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(value = "common.configuration.kafka-event.enabled", matchIfMissing = true)
@Component
public class KafkaTopicNameResolver {


    public String getTopicName(SwaggerItemCollector item) {
        return String.join(".", item.getTitle(), item.getGroup(), item.getVersion(), item.getResourceName(), item.getHttpMethod().name(), item.getOperationName());
    }

}
