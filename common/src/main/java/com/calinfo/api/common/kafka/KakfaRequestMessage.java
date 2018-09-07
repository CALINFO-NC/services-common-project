package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakfaRequestMessage<T> {

    private Object[] params;
    private String resultType;
    private T result;

}
