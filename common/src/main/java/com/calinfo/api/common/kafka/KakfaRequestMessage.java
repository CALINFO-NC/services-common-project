package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
public class KakfaRequestMessage<T> {

    private Object[] params;
    private String resultType;
    private T result;

}
