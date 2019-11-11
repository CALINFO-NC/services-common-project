package com.calinfo.api.common.mocks;

import com.calinfo.api.common.MessageCodeValue;
import com.calinfo.api.common.service.MessageService;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by dalexis on 20/11/2017.
 */
public class MockMessageServiceImpl implements MessageService {

    public String translate(Locale locale, MessageCodeValue codeMessage, Serializable... params){
        return locale.getLanguage() + "_" + codeMessage.name();
    }
}
