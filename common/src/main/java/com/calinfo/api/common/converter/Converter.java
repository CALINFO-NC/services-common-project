package com.calinfo.api.common.converter;

/**
 * Created by dalexis on 19/05/2018.
 */
public interface Converter {

    boolean accept(Class<?> source, Class<?> dest);

}
