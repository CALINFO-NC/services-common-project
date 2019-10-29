package com.calinfo.api.common.io.mock;

import com.calinfo.api.common.MessageCodeValue;
import com.calinfo.api.common.service.MessageService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Locale;

@Service
public class MockMessageService implements MessageService {

    @Override
    public String translate(Locale locale, MessageCodeValue codeMessage, Serializable... params) {
        return codeMessage.name();
    }
}
