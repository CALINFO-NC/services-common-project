package com.calinfo.api.common.dto;

import java.util.List;

public interface MessageInfoAndWarningInterface {

    List<String> getListInfoMessages();

    void setListInfoMessages(List<String> listInfoMessages);

    List<String> getListWarningMessages();

    void setListWarningMessages(List<String> listWarningMessages);
}
