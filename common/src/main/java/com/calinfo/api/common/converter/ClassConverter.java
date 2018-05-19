package com.calinfo.api.common.converter;

/**
 * Created by dalexis on 19/05/2018.
 */
public interface ClassConverter extends Converter{

    <T> T convert(Object source, Class<T> dest);

}
