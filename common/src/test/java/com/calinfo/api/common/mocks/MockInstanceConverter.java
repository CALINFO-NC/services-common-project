package com.calinfo.api.common.mocks;

import com.calinfo.api.common.converter.ContextConverter;
import com.calinfo.api.common.converter.InstanceConverter;
import lombok.Getter;

/**
 * Created by dalexis on 21/05/2018.
 */
public class MockInstanceConverter implements InstanceConverter {

    @Getter
    private Class<?> sourceClass;

    @Getter
    private Class<?> destClass;

    @Getter
    private Object sourceConvert;

    @Getter
    private Object destConvert;

    @Getter
    private ContextConverter contextConverter;

    @Override
    public boolean accept(Class<?> source, Class<?> dest) {
        this.sourceClass = source;
        this.destClass = dest;
        return true;
    }

    @Override
    public <T> T convert(Object source, T dest, ContextConverter contextConverter) {
        this.sourceConvert = source;
        this.destConvert = dest;
        this.contextConverter = contextConverter;
        return dest;
    }
}
