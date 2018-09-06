package com.calinfo.api.common.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class KafkaErrorMessage {

    private String message;
    private HttpStatus status;


}
