package com.calinfo.api.common.swagger;

import com.calinfo.api.common.validation.Create;
import com.calinfo.api.common.validation.Update;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import javax.validation.constraints.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dalexis on 08/06/2018.
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 10)
public class ReadOnlyPropertyGenericResource implements ModelPropertyBuilderPlugin {

    private static final Logger log = LoggerFactory.getLogger(ReadOnlyPropertyGenericResource.class);


    @Override
    public void apply(ModelPropertyContext modelPropertyContext) {


        ModelPropertyBuilder builder = modelPropertyContext.getBuilder();

        if (modelPropertyContext.getBeanPropertyDefinition().isPresent()) {

            // Vérifier qu'un champ est requis
            BeanPropertyDefinition beanDef = modelPropertyContext.getBeanPropertyDefinition().get();
            Field field = beanDef.getField().getAnnotated();

            Object anno = field.getAnnotation(Null.class);
            String description = getAnnotationText("readOnly", "true", anno);

            anno = field.getAnnotation(NotNull.class);
            description = concatTextDescrption(description, getAnnotationText("required", "true", anno));

            anno = field.getAnnotation(Email.class);
            description = concatTextDescrption(description, getAnnotationText("valueType", "email", anno));

            anno = field.getAnnotation(Size.class);
            description = concatTextDescrption(description, getAnnotationText("minLength", getAnnotationValue(anno, "min"), anno));
            description = concatTextDescrption(description, getAnnotationText("maxLength", getAnnotationValue(anno, "max"), anno));

            anno = field.getAnnotation(Min.class);
            description = concatTextDescrption(description, getAnnotationText("minValue", getAnnotationValue(anno, "value"), anno));

            anno = field.getAnnotation(Max.class);
            description = concatTextDescrption(description, getAnnotationText("maxValue", getAnnotationValue(anno, "value"), anno));

            anno = field.getAnnotation(NotBlank.class);
            description = concatTextDescrption(description, getAnnotationText("blank","false", anno));

            anno = field.getAnnotation(Pattern.class);
            description = concatTextDescrption(description, getAnnotationText("pattern", getAnnotationValue(anno, "regexp"), anno));

            anno = field.getAnnotation(Negative.class);
            description = concatTextDescrption(description, getAnnotationText("negativeValueOnly", "true", anno));

            anno = field.getAnnotation(NegativeOrZero.class);
            description = concatTextDescrption(description, getAnnotationText("negativeOrZeroValueOnly", "true", anno));

            anno = field.getAnnotation(Positive.class);
            description = concatTextDescrption(description, getAnnotationText("positiveValueOnly", "true", anno));

            anno = field.getAnnotation(PositiveOrZero.class);
            description = concatTextDescrption(description, getAnnotationText("positiveOrZeroValueOnly", "true", anno));

            anno = field.getAnnotation(PastOrPresent.class);
            description = concatTextDescrption(description, getAnnotationText("pastOrPresentOnly", "true", anno));

            anno = field.getAnnotation(Past.class);
            description = concatTextDescrption(description, getAnnotationText("pastOnly", "true", anno));

            anno = field.getAnnotation(NotEmpty.class);
            description = concatTextDescrption(description, getAnnotationText("empty", "false", anno));

            anno = field.getAnnotation(FutureOrPresent.class);
            description = concatTextDescrption(description, getAnnotationText("futureOrPresentOnly", "true", anno));

            anno = field.getAnnotation(Future.class);
            description = concatTextDescrption(description, getAnnotationText("futureOnly", "true", anno));

            anno = field.getAnnotation(DecimalMin.class);
            description = concatTextDescrption(description, getAnnotationText("minDecimalValue", getAnnotationValue(anno, "value"), anno));

            anno = field.getAnnotation(DecimalMax.class);
            description = concatTextDescrption(description, getAnnotationText("maxDecimalValue", getAnnotationValue(anno, "value"), anno));

            anno = field.getAnnotation(AssertFalse.class);
            description = concatTextDescrption(description, getAnnotationText("falseOnly", "true", anno));

            anno = field.getAnnotation(AssertTrue.class);
            description = concatTextDescrption(description, getAnnotationText("trueOnly", "true", anno));

            anno = field.getAnnotation(Digits.class);
            description = concatTextDescrption(description, getAnnotationText("digitIntegerValue", getAnnotationValue(anno, "integer"), anno));
            description = concatTextDescrption(description, getAnnotationText("digitFractionValue", getAnnotationValue(anno, "fraction"), anno));

            description = concatTextDescrption(description, getFieldDescription(field));
            builder.description(description);
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }

    private Object getAnnotationValue(Object annotation, String methodName){

        if (annotation != null){

            try {
                Method mGroups = annotation.getClass().getMethod(methodName);
                return mGroups.invoke(annotation);

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }


        }

        return null;
    }

    private String getAnnotationText(String propertyName, Object propertyValue, Object annotation){

        if (annotation != null){

            Object objGroups = getAnnotationValue(annotation, "groups");
            Class<?>[] groups = (Class<?>[]) objGroups;

            String description = null;

            List<?> lstGroup = Arrays.asList(groups);

            String propValue = "";
            if (propertyValue != null){
                propValue = propertyValue.toString();
            }

            if (lstGroup.contains(Create.class)){
                description = concatTextDescrption(description, String.format("%s: %s (on create)", propertyName, propValue));
            }
            else if (lstGroup.contains(Update.class)){
                description = concatTextDescrption(description, String.format("%s: %s (on update)", propertyName, propValue));
            }
            else {
                description = concatTextDescrption(description, String.format("%s: %s", propertyName, propValue));
            }

            return String.format("<span style=\"color:gray;\">%s</span>", description);


        }

        return null;
    }

    private String getFieldDescription(Field field){

        ApiModelProperty apiModelPropertyAno = field.getAnnotation(ApiModelProperty.class);

        if (apiModelPropertyAno == null || StringUtils.isBlank(apiModelPropertyAno.value())){
            return null;
        }

        return apiModelPropertyAno.value();
    }

    private String concatTextDescrption(String base, String newDesc){

        if (StringUtils.isBlank(newDesc)){
            return base;
        }

        if (StringUtils.isBlank(base)){
            return newDesc;
        }

        return String.join("<br/>", base, newDesc);
    }
}
