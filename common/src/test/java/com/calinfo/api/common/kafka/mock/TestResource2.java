package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.dto.Dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestResource2<T> implements Dto {

    private T prop1;

    private int prop2;

    @JsonIgnore
    private int prop3;
}
