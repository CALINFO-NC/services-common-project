package com.calinfo.api.common.swagger;

import com.calinfo.api.common.security.CommonSecurityUrlFilter;
import com.calinfo.api.common.security.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dalexis on 08/06/2018.
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 11)
public class SecurityFilterSwaggerOperation implements OperationBuilderPlugin {

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void apply(OperationContext operationContext) {

        final List<Parameter> parameters = new LinkedList<>();

        // Uniquement sur les URL privée
        if (operationContext.requestMappingPattern().matches(securityProperties.getPrivateUrlRegex())){

            Parameter parameter = new ParameterBuilder()
                    .name(CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME)
                    .description(CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME)
                    .modelRef(new ModelRef("string"))
                    .parameterType("header")
                    .required(true)
                    .build();
            parameters.add(parameter);
        }


        operationContext.operationBuilder().parameters(parameters);
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
