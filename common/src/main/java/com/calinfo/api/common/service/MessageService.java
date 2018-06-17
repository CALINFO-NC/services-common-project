package com.calinfo.api.common.service;

import com.calinfo.api.common.MessageCodeValue;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by dalexis on 20/11/2017.
 */

public interface MessageService {


    String translate(Locale locale, MessageCodeValue codeMessage, Serializable... params);

    default String translate(MessageCodeValue codeMessage, Serializable... params){
        return translate(Locale.getDefault(), codeMessage, params);
    }
}
