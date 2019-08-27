package com.calinfo.api.common.swagger;

import com.calinfo.api.common.security.CommonSecurityUrlFilter;
import com.calinfo.api.common.security.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Header;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

import java.util.*;

/**
 * Created by dalexis on 08/06/2018.
 */

@Component
@Order(SecurityFilterSwaggerOperationBuilder.ORDER)
@ConditionalOnClass({ApiInfo.class})
public class SecurityFilterSwaggerOperationBuilder implements OperationBuilderPlugin {

    public static final int ORDER = CollectorOperationBuilder.ORDER + 100;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private SwaggerCollector swaggerCollector;

    @Override
    public void apply(OperationContext operationContext) {

        ModelRef tokenModel = new ModelRef("string");
        String tokenParameterType = "header";

        final List<Parameter> parameters = new LinkedList<>();
        Set<ResponseMessage> responseMessage = operationContext.operationBuilder().build().getResponseMessages();

        // Uniquement sur les URL privée
        if (securityProperties.isEnabled() && operationContext.requestMappingPattern().matches(securityProperties.getPrivateUrlRegex())){

            Parameter parameter = new ParameterBuilder()
                    .name(CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME)
                    .description(String.format("%s prefixed by '%s'", CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME, CommonSecurityUrlFilter.BEARER_PREFIX))
                    .modelRef(tokenModel)
                    .parameterType(tokenParameterType)
                    .required(true)
                    .build();
            parameters.add(parameter);

            parameter = new ParameterBuilder()
                    .name(CommonSecurityUrlFilter.HEADER_API_KEY)
                    .description(CommonSecurityUrlFilter.HEADER_API_KEY)
                    .modelRef(tokenModel)
                    .parameterType(tokenParameterType)
                    .required(true)
                    .build();
            parameters.add(parameter);

            // Ajout de l'API Key en réponse sur le 200
            Iterator<ResponseMessage> it = responseMessage.iterator();
            while (it.hasNext()){
                ResponseMessage rm = it.next();

                if (rm.getCode() == 500){
                    continue;
                }

                Map<String, Header> headers = rm.getHeaders();
                Header header = new Header(CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME, String.format("New %s token prefixed by '%s'", CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME, CommonSecurityUrlFilter.BEARER_PREFIX), tokenModel);
                headers.put(CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME, header);
            }
        }


        operationContext.operationBuilder().parameters(parameters);
        operationContext.operationBuilder().responseMessages(responseMessage);
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
