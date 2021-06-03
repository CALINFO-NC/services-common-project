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

import com.calinfo.api.common.utils.MiscUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * Created by dalexis on 08/06/2018.
 */

@Component
@Order(DefaultNicknameOperationBuilder.ORDER)
@ConditionalOnClass({ApiInfo.class})
public class DefaultNicknameOperationBuilder implements OperationBuilderPlugin {

    public static final int ORDER = SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 100;

    @Override
    public void apply(OperationContext operationContext) {
        operationContext.operationBuilder().codegenMethodNameStem(MiscUtils.getNickNameSwagger(operationContext));
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
