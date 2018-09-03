package com.calinfo.api.common.swagger;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SwaggerCollector {

    private Map<String, List<SwaggerItemCollector>> mapItempCollector = new HashMap<>();

    public void add(SwaggerItemCollector item){
        mapItempCollector.computeIfAbsent(item.getUri(), k -> new ArrayList<SwaggerItemCollector>()).add(item);
    }

    public List<SwaggerItemCollector> getSwaggerItemByUri(String uri){
        List<SwaggerItemCollector> result = mapItempCollector.get(uri);

        return result == null ? new ArrayList<>() : result;
    }
}
