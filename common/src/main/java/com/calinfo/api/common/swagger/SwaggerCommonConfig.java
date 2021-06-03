package com.calinfo.api.common.swagger;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
