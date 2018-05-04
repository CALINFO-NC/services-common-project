package com.calinfo.api.common.mocks;

import com.calinfo.api.common.MessageCodeValue;
import com.calinfo.api.common.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Created by dalexis on 20/11/2017.
 */

@Service
public class MockMessageServiceImpl implements MessageService {

    public String translate(Locale locale, MessageCodeValue codeMessage, Object... params){
        return locale.getLanguage() + "_" + codeMessage.name();
    }
}
