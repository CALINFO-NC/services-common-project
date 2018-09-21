package com.calinfo.api.common.swagger;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Component
public class SwaggerCollector {

    private Map<String, List<SwaggerItemCollector>> mapItemCollector = new HashMap<>();

    public void add(SwaggerItemCollector item){
        mapItemCollector.computeIfAbsent(item.getUri(), k -> new ArrayList<SwaggerItemCollector>()).add(item);
    }

    public List<SwaggerItemCollector> getSwaggerItemByUri(String uri){
        List<SwaggerItemCollector> result = mapItemCollector.get(uri);

        return result == null ? new ArrayList<>() : result;
    }

    public List<SwaggerItemCollector> getAll(){

        List<SwaggerItemCollector> result = new ArrayList<>();
        for (Map.Entry<String, List<SwaggerItemCollector>> entry: mapItemCollector.entrySet()){
            result.addAll(entry.getValue());
        }

        return result;
    }
}
