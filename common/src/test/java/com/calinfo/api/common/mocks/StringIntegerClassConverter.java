package com.calinfo.api.common.mocks;

import com.calinfo.api.common.converter.ClassConverter;
import com.calinfo.api.common.converter.ContextConverter;

/**
 * Created by dalexis on 21/05/2018.
 */
public class StringIntegerClassConverter implements ClassConverter {



    @Override
    public boolean accept(Class<?> source, Class<?> dest, ContextConverter contextConverter) {
        return true;
    }

    @Override
    public <T> T convert(Object source, Class<T> dest, ContextConverter contextConverter) {
        return (source instanceof String) ? (T)Integer.valueOf(source.toString()) : (T)source.toString();
    }
}
