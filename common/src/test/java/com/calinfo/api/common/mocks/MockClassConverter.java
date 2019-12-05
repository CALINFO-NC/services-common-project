package com.calinfo.api.common.mocks;

import com.calinfo.api.common.converter.ClassConverter;
import com.calinfo.api.common.converter.ContextConverter;
import lombok.Getter;

/**
 * Created by dalexis on 21/05/2018.
 */
public class MockClassConverter implements ClassConverter {

    @Getter
    private Class<?> sourceClass;

    @Getter
    private Class<?> destClass;

    @Getter
    private Object sourceConvert;

    @Getter
    private Class<?> destConvert;

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
    public <T> T convert(Object source, Class<T> dest, ContextConverter contextConverter) {
        this.sourceConvert = source;
        this.destConvert = dest;
        this.contextConverterConvert = contextConverter;
        return null;
    }
}
