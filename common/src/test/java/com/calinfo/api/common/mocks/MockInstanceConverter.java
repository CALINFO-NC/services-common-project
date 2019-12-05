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
    private ContextConverter contextConverterConvert;

    @Getter
    private ContextConverter contextConverterAccept;

    @Override
    public boolean accept(Class<?> source, Class<?> dest, ContextConverter contextConverter) {
        this.sourceClass = source;
        this.destClass = dest;
        this.contextConverterAccept = contextConverter;
        return true;
    }

    @Override
    public <T> T convert(Object source, T dest, ContextConverter contextConverter) {
        this.sourceConvert = source;
        this.destConvert = dest;
        this.contextConverterConvert = contextConverter;
        return dest;
    }
}
