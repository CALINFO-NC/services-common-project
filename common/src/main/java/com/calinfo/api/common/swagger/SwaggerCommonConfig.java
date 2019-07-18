package com.calinfo.api.common.swagger;

import com.calinfo.api.common.dto.BadRequestParameterDto;
import com.calinfo.api.common.dto.BadResponseDto;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;


@Component
@ConditionalOnClass({ApiInfo.class})
public class SwaggerCommonConfig implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName){

        if (bean.getClass().isAssignableFrom(Docket.class)){
            Docket docket = (Docket) bean;

            TypeResolver typeResolver = new TypeResolver();
            ResolvedType badRequestParameterResourceType = typeResolver.resolve(BadRequestParameterDto.class); // 400
            ResolvedType badResponseResourceType = typeResolver.resolve(BadResponseDto.class); // 501

            docket.additionalModels(badRequestParameterResourceType);
            docket.additionalModels(badResponseResourceType);

            List<ResponseMessage> lstDefaultResponse = new ArrayList<>();
            lstDefaultResponse.add(
                    new ResponseMessageBuilder()
                            .code(200)
                            .message("OK.").build());
            lstDefaultResponse.add(
                    new ResponseMessageBuilder()
                            .code(400)
                            .responseModel(new ModelRef(String.format("#/definitions/%s", BadRequestParameterDto.class.getSimpleName())))
                            .message("Bad parameters request.").build());
            lstDefaultResponse.add(
                    new ResponseMessageBuilder()
                            .code(401)
                            .message("Unauthorized.").build());
            lstDefaultResponse.add(
                    new ResponseMessageBuilder()
                            .code(403)
                            .message("Forbidden.").build());
            lstDefaultResponse.add(
                    new ResponseMessageBuilder()
                            .code(500)
                            .message("Internal server error.").build());
            lstDefaultResponse.add(
                    new ResponseMessageBuilder()
                            .code(501)
                            .responseModel(new ModelRef(String.format("#/definitions/%s", BadResponseDto.class.getSimpleName())))
                            .message("Bad use case.").build());
            lstDefaultResponse.add(
                    new ResponseMessageBuilder()
                            .code(404)
                            .message("URL or Resource not found.").build());

            docket.globalResponseMessage(RequestMethod.GET, lstDefaultResponse);
            docket.globalResponseMessage(RequestMethod.POST, lstDefaultResponse);
            docket.globalResponseMessage(RequestMethod.PUT, lstDefaultResponse);
            docket.globalResponseMessage(RequestMethod.DELETE, lstDefaultResponse);

        }

        return bean;

    }
}
