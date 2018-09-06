package com.calinfo.api.common.kafka;

import com.calinfo.api.common.swagger.SwaggerCollector;
import com.calinfo.api.common.swagger.SwaggerItemCollector;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
     * @param methods
     * @return liste des noms de topics a crée pour la liste des uri (patterns) fournis en paramètre
     */
    public Set<String> resolve(Set<String> patterns, String className, String methodName, Set<RequestMethod> methods) {

        Set<String> result = patterns
                .stream()
                .flatMap(uri -> this.swaggerCollector.getSwaggerItemByUri(uri)
                        .stream()
                        .filter(item ->
                                methods.stream()
                                        .anyMatch(method -> method.name().equals(item.getHttpMethod().name()))
                        ))
                .map(KafkaTopicNameResolver::toTopicName)
                .distinct()
                .collect(Collectors.toSet());

        result.stream()
                .findFirst()
                .ifPresent(topicName -> topicControllerMapping.put(className + "." + methodName, topicName));

        return result;
    }


    public static String toTopicName(SwaggerItemCollector item) {
        return item.getTitle() + "." + item.getGroup() + "." + item.getVersion() + "." + item.getResourceName() + "." + item.getOperationName();
    }


    public String getTopic(String className, String methodName) {
        return topicControllerMapping.get(className + "." + methodName);
    }
}
