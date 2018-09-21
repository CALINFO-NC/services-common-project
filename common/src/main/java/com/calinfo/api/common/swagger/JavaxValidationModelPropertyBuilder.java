package com.calinfo.api.common.swagger;

import com.calinfo.api.common.validation.Create;
import com.calinfo.api.common.validation.Update;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import javax.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dalexis on 08/06/2018.
 */
@Lazy
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 10)
@ConditionalOnClass({ApiInfo.class})
public class JavaxValidationModelPropertyBuilder implements ModelPropertyBuilderPlugin {

    private static final Logger log = LoggerFactory.getLogger(JavaxValidationModelPropertyBuilder.class);


    @Override
    public void apply(ModelPropertyContext modelPropertyContext) {


        ModelPropertyBuilder builder = modelPropertyContext.getBuilder();

        if (modelPropertyContext.getBeanPropertyDefinition().isPresent()) {

            // Vérifier qu'un champ est requis
            BeanPropertyDefinition beanDef = modelPropertyContext.getBeanPropertyDefinition().get();
            Field field = beanDef.getField().getAnnotated();

            Object anno = getFieldAccessorAnnotation(field, Null.class);
            String description = getAnnotationText("readOnly", "true", anno);

            anno = getFieldAccessorAnnotation(field, NotNull.class);
            description = concatTextDescrption(description, getAnnotationText("required", "true", anno));

            anno = getFieldAccessorAnnotation(field, Email.class);
            description = concatTextDescrption(description, getAnnotationText("valueType", "email", anno));

            anno = getFieldAccessorAnnotation(field, Size.class);
            description = concatTextDescrption(description, getAnnotationText("minLength", getAnnotationValue(anno, "min"), anno));
            description = concatTextDescrption(description, getAnnotationText("maxLength", getAnnotationValue(anno, "max"), anno));

            anno = getFieldAccessorAnnotation(field, Min.class);
            description = concatTextDescrption(description, getAnnotationText("minValue", getAnnotationValue(anno), anno));

            anno = getFieldAccessorAnnotation(field, Max.class);
            description = concatTextDescrption(description, getAnnotationText("maxValue", getAnnotationValue(anno), anno));

            anno = getFieldAccessorAnnotation(field, NotBlank.class);
            description = concatTextDescrption(description, getAnnotationText("blank", "false", anno));

            anno = getFieldAccessorAnnotation(field, Pattern.class);
            description = concatTextDescrption(description, getAnnotationText("pattern", getAnnotationValue(anno, "regexp"), anno));

            anno = getFieldAccessorAnnotation(field, Negative.class);
            description = concatTextDescrption(description, getAnnotationText("negativeValueOnly", "true", anno));

            anno = getFieldAccessorAnnotation(field, NegativeOrZero.class);
            description = concatTextDescrption(description, getAnnotationText("negativeOrZeroValueOnly", "true", anno));

            anno = getFieldAccessorAnnotation(field, Positive.class);
            description = concatTextDescrption(description, getAnnotationText("positiveValueOnly", "true", anno));

            anno = getFieldAccessorAnnotation(field, PositiveOrZero.class);
            description = concatTextDescrption(description, getAnnotationText("positiveOrZeroValueOnly", "true", anno));

            anno = getFieldAccessorAnnotation(field, PastOrPresent.class);
            description = concatTextDescrption(description, getAnnotationText("pastOrPresentOnly", "true", anno));

            anno = getFieldAccessorAnnotation(field, Past.class);
            description = concatTextDescrption(description, getAnnotationText("pastOnly", "true", anno));

            anno = getFieldAccessorAnnotation(field, NotEmpty.class);
            description = concatTextDescrption(description, getAnnotationText("empty", "false", anno));

            anno = getFieldAccessorAnnotation(field, FutureOrPresent.class);
            description = concatTextDescrption(description, getAnnotationText("futureOrPresentOnly", "true", anno));

            anno = getFieldAccessorAnnotation(field, Future.class);
            description = concatTextDescrption(description, getAnnotationText("futureOnly", "true", anno));

            anno = getFieldAccessorAnnotation(field, DecimalMin.class);
            description = concatTextDescrption(description, getAnnotationText("minDecimalValue", getAnnotationValue(anno), anno));

            anno = getFieldAccessorAnnotation(field, DecimalMax.class);
            description = concatTextDescrption(description, getAnnotationText("maxDecimalValue", getAnnotationValue(anno), anno));

            anno = getFieldAccessorAnnotation(field, AssertFalse.class);
            description = concatTextDescrption(description, getAnnotationText("falseOnly", "true", anno));

            anno = getFieldAccessorAnnotation(field, AssertTrue.class);
            description = concatTextDescrption(description, getAnnotationText("trueOnly", "true", anno));

            anno = getFieldAccessorAnnotation(field, Digits.class);
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

    private <T extends Annotation> T getFieldAccessorAnnotation(Field field, Class<T> clazz){

        T result = field.getAnnotation(clazz);

        // Si pas d'annotation, alors récupérer celui du getter
        if (result == null){
            String getterPrefix = field.getType().equals(boolean.class) ? "is" : "get";
            String getterName = String.format("%s%s", getterPrefix, StringUtils.capitalize(field.getName()));

            try {
                Method m = field.getDeclaringClass().getMethod(getterName);
                result = m.getAnnotation(clazz);
            } catch (NoSuchMethodException e) {
                log.info(e.getMessage(), e);
            }
        }

        // Si pas d'annotation, alors récupérer celui du setter
        if (result == null){
            String setterName = String.format("set%s", StringUtils.capitalize(field.getName()));

            try {
                Method m = field.getDeclaringClass().getMethod(setterName, field.getType());
                result = m.getAnnotation(clazz);
            } catch (NoSuchMethodException e) {
                log.info(e.getMessage(), e);
            }
        }

        return result;
    }

    private Object getAnnotationValue(Object annotation) {
        return getAnnotationValue(annotation, "value");
    }

    private Object getAnnotationValue(Object annotation, String methodName) {

        if (annotation != null) {

            try {
                Method mGroups = annotation.getClass().getMethod(methodName);
                return mGroups.invoke(annotation);

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }


        }

        return null;
    }

    private String getAnnotationText(String propertyName, Object propertyValue, Object annotation) {

        if (annotation != null) {

            Object objGroups = getAnnotationValue(annotation, "groups");
            Class<?>[] groups = (Class<?>[]) objGroups;

            String description = null;

            List<?> lstGroup = Arrays.asList(groups);

            String propValue = "";
            if (propertyValue != null) {
                propValue = propertyValue.toString();
            }

            if (lstGroup.contains(Create.class)) {
                description = concatTextDescrption(description, String.format("%s: %s (on create)", propertyName, propValue));
            } else if (lstGroup.contains(Update.class)) {
                description = concatTextDescrption(description, String.format("%s: %s (on update)", propertyName, propValue));
            } else {
                description = concatTextDescrption(description, String.format("%s: %s", propertyName, propValue));
            }

            return String.format("<span style=\"color:gray;\">%s</span>", description);


        }

        return null;
    }

    private String getFieldDescription(Field field) {

        ApiModelProperty apiModelPropertyAno = getFieldAccessorAnnotation(field, ApiModelProperty.class);

        if (apiModelPropertyAno == null || StringUtils.isBlank(apiModelPropertyAno.value())) {
            return null;
        }

        return apiModelPropertyAno.value();
    }

    private String concatTextDescrption(String base, String newDesc) {

        if (StringUtils.isBlank(newDesc)) {
            return base;
        }

        if (StringUtils.isBlank(base)) {
            return newDesc;
        }

        return String.join("<br/>", base, newDesc);
    }
}
