package com.calinfo.api.common.swagger;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class SwaggerCollector {

    private List<SwaggerItemCollector> lstItemCollector = new ArrayList<>();

    public void add(SwaggerItemCollector item){
        lstItemCollector.add(item);
    }


    public List<SwaggerItemCollector> getAll(){

        return lstItemCollector;
    }
}
