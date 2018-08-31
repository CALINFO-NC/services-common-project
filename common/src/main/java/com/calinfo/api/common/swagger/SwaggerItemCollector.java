package com.calinfo.api.common.swagger;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Getter
@Setter
public class SwaggerItemCollector {


    private String uri;

    private String operationName;

    private String resourceName;

    private HttpMethod httpMethod;

    private String group;

    private String title;

    private String version;

}
