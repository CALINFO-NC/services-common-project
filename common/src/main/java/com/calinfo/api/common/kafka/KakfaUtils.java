package com.calinfo.api.common.kafka;

import java.lang.reflect.Method;

class KakfaUtils {

    public static String topicName(String appName, Class<?> clazz, String method) {

        // TODO Remplacer par la classe qui récupère les informations
        return appName + "." + clazz.getSimpleName() + "." + method;
    }
}
