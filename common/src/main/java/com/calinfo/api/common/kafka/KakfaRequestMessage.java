package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class KakfaRequestMessage<T> {

    private Object[] params;
    private String resultType;
    private T result;
    // Gestion des erreurs
    private boolean isError = false;
    private String message;
    private HttpStatus status;

}
