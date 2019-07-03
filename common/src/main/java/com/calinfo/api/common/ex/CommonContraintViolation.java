package com.calinfo.api.common.ex;

import org.hibernate.validator.internal.engine.path.PathImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

public class CommonContraintViolation implements ConstraintViolation<Object> {

    private String propertyPath;

    private String message;

    public CommonContraintViolation(String propertyPath, String message){
        this.propertyPath = propertyPath;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getMessageTemplate() {
        return null;
    }

    @Override
    public Object getRootBean() {
        return null;
    }

    @Override
    public Class<Object> getRootBeanClass() {
        return null;
    }

    @Override
    public Object getLeafBean() {
        return null;
    }

    @Override
    public Object[] getExecutableParameters() {
        return new Object[0];
    }

    @Override
    public Object getExecutableReturnValue() {
        return null;
    }

    @Override
    public Path getPropertyPath() {
        return PathImpl.createPathFromString(propertyPath);
    }

    @Override
    public Object getInvalidValue() {
        return null;
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return null;
    }

    @Override
    public <U> U unwrap(Class<U> aClass) {
        return null;
    }
}
