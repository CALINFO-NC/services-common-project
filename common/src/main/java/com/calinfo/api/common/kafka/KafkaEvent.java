package com.calinfo.api.common.kafka;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
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

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class KafkaEvent {

    @Getter
    @Setter
    private String domain;

    @Getter
    @Setter
    private KafkaUser user;

    @Getter
    @Setter
    private KafkaApplication application;

    @Getter
    @Setter
    private String topic;

    @Getter
    @Setter
    private String fullQualifiedServiceClassName;

    @Getter
    @Setter
    private String methodServiceName;

    @Getter
    @Setter
    private Map<Integer, KafkaObject> parameters = new HashMap<>();

    @Getter
    private KafkaObject result;

    @Getter
    @Setter
    private boolean resultException;

    private KafkaException cache;

    public <T> T get() {

        if (isResultException()){
            throwException();
        }

        return (T)getResult().get();
    }

    private void throwException() {

        if (cache != null){
            throw cache;
        }

        try {
            Class<?> clazz = Class.forName(result.getFullQualifiedClassName());
            Constructor<?> constructor = clazz.getConstructor(String.class);
            Exception cause = (Exception)constructor.newInstance(result.getStrValue());
            cache = new KafkaInvocationException(result.getFullQualifiedClassName(), cause);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            String msg = getResult().get();
            ClassNotFoundException cause = new ClassNotFoundException(msg, e);
            cache = new KafkaRestitutionException(result.getFullQualifiedClassName(), cause);
        }

        throw cache;
    }

    public void setResult(KafkaObject result){
        this.result = result;
        cache = null;
    }
}
