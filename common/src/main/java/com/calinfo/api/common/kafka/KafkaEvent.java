package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class KafkaEvent {

    private String domain;
    private KafkaUser user;

    private String topic;
    private String fullQualifiedServiceClassName;
    private String methodServiceName;

    private Set<KafkaObject> parameters = new HashSet<>();
    private KafkaObject result;

    private boolean resultException;

    public <T> T get() {

        if (isResultException()){
            throwException();
        }

        return (T)getResult().get();
    }

    private void throwException() {

        KafkaException ex;
        try {
            Class<?> clazz = Class.forName(result.getFullQualifiedClassName());
            Constructor<?> constructor = clazz.getConstructor(String.class);
            Exception cause = (Exception)constructor.newInstance(result.getValue());
            ex = new KafkaException(result.getFullQualifiedClassName(), cause, true);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            String msg = getResult().get();
            ClassNotFoundException cause = new ClassNotFoundException(msg, e);
            ex = new KafkaException(result.getFullQualifiedClassName(), cause, false);
        }

        throw ex;
    }
}
