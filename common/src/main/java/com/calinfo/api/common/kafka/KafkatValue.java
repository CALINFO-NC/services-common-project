package com.calinfo.api.common.kafka;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2023 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.calinfo.api.common.ex.ApplicationErrorException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class KafkatValue {

    private final KafkaMetadataService metadataService;
    private final KafkaData data;

    public int getParametersSize(){
        return metadataService.getParametersTypes().size();
    }

    public <T> T getParameterValueAt(int index, Class<T> clazz){
        return get(clazz, data.getSerializedParametersValues().get(index));
    }

    public <T> T getParameterValueAt(int index) throws ClassNotFoundException {
        Class<T> clazz = (Class<T>) Class.forName(metadataService.getParametersTypes().get(index));
        return getParameterValueAt(index, clazz);
    }

    public <T> T getReturnValue(Class<T> clazz){

        if (data.isReturnValueException()){
            throwException();
        }

        return get(clazz, data.getSerializedReturnValue());
    }

    public <T> T getReturnValue() throws ClassNotFoundException {
        Class<T> clazz = (Class<T>) Class.forName(metadataService.getReturnType());
        return getReturnValue(clazz);
    }

    private  <T> T get(Class<T> clazz, String serializedValue) {
        try {
            return KafkaUtils.unserialize(clazz, serializedValue);
        } catch (IOException e) {
            throw new ApplicationErrorException(e);
        }
    }

    private void throwException() throws KafkaException {

        try {
            Class<?> clazz = Class.forName(metadataService.getReturnType());
            Constructor<?> constructor = clazz.getConstructor(String.class);
            Exception cause = (Exception)constructor.newInstance(data.getSerializedReturnValue());
            throw new KafkaInvocationException(metadataService.getReturnType(), cause);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            ClassNotFoundException cause = new ClassNotFoundException(data.getSerializedReturnValue(), e);
            throw new KafkaRestitutionException(metadataService.getReturnType(), cause);
        }
    }
}
