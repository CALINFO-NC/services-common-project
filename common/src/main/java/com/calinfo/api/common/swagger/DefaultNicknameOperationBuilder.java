package com.calinfo.api.common.swagger;

import com.google.common.base.Optional;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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

        String nickname = "";
        Optional<ApiOperation> apiOperation = operationContext.findAnnotation(ApiOperation.class);
        if (apiOperation.isPresent()){
            nickname = apiOperation.get().nickname();
        }

        if (StringUtils.isBlank(nickname)) {
            nickname = operationContext.getName();
            operationContext.operationBuilder().codegenMethodNameStem(nickname);
        }

    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
