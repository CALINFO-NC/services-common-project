package com.calinfo.api.common.mocks;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;


@Getter
@Setter
public class MockDtoContrainteViolation {

    @NotNull
    private String prop1;

    @Null
    private String prop2;

    @Valid
    private MockDtoInerContrainteViolation prop3;
}
