package com.calinfo.api.common.kafka;

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
