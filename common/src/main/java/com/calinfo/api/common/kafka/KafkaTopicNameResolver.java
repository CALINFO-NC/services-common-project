package com.calinfo.api.common.kafka;

import com.calinfo.api.common.swagger.SwaggerCollector;
import com.calinfo.api.common.swagger.SwaggerItemCollector;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class KafkaTopicNameResolver {


    private final SwaggerCollector swaggerCollector;
    private final ConcurrentHashMap<String, String> topicControllerMapping = new ConcurrentHashMap<>();


    public KafkaTopicNameResolver(SwaggerCollector swaggerCollector) {
        this.swaggerCollector = swaggerCollector;
    }

    /**
     * @param patterns
     * @param className
     * @param methodName
     * @return liste des noms de topics a crée pour la liste des uri (patterns) fournis en paramètre
     */
    public Set<String> resolve(Set<String> patterns, String className, String methodName) {

        Set<String> result = patterns
                .stream()
                .flatMap(uri -> this.swaggerCollector.getSwaggerItemByUri(uri).stream())
                .map(KafkaTopicNameResolver::toTopicName)
                .distinct()
                .collect(Collectors.toSet());

        result.stream()
                .findFirst()
                .ifPresent(topicName -> topicControllerMapping.put(className + "." + methodName, topicName));

        return result;
    }


    public static String toTopicName(SwaggerItemCollector item) {
        return item.getTitle() + "." + item.getGroup() + "." + item.getResourceName() + "." + item.getOperationName();
    }


    public String getTopic(String className, String methodName) {
        return topicControllerMapping.get(className + "." + methodName);
    }
}
