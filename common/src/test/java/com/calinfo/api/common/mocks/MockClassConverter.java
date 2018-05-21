package com.calinfo.api.common.mocks;

import com.calinfo.api.common.converter.ClassConverter;
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


    @Override
    public boolean accept(Class<?> source, Class<?> dest) {
        this.sourceClass = source;
        this.destClass = dest;
        return true;
    }

    @Override
    public <T> T convert(Object source, Class<T> dest) {
        this.sourceConvert = source;
        this.destConvert = dest;
        return null;
    }
}
