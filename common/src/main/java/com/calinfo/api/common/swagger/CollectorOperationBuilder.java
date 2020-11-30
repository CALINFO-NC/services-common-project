package com.calinfo.api.common.swagger;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2020 CALINFO
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

import com.calinfo.api.common.utils.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

/**
 * Created by dalexis on 08/06/2018.
 */

@Deprecated(since = "1.2.0", forRemoval = true)
@Component
@Order(CollectorOperationBuilder.ORDER)
@ConditionalOnClass({ApiInfo.class})
@ConditionalOnProperty(prefix = "common.deprecated.swagger", name = "enabled", havingValue = "true")
public class CollectorOperationBuilder implements OperationBuilderPlugin {

    public static final int ORDER = DefaultNicknameOperationBuilder.ORDER + 100;

    @Autowired
    private SwaggerCollector collector;

    @Override
    public void apply(OperationContext operationContext) {

        for (String tag : operationContext.operationBuilder().build().getTags()) {
            SwaggerItemCollector itemCollector = new SwaggerItemCollector();
            itemCollector.setOperationName(MiscUtils.getNickNameSwagger(operationContext));
            itemCollector.setResourceName(tag);
            itemCollector.setUri(operationContext.requestMappingPattern());
            itemCollector.setGroup(operationContext.getDocumentationContext().getGroupName());
            itemCollector.setHttpMethod(operationContext.httpMethod());
            itemCollector.setVersion(operationContext.getDocumentationContext().getApiInfo().getVersion());
            itemCollector.setTitle(operationContext.getDocumentationContext().getApiInfo().getTitle());
            collector.add(itemCollector);
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
