package com.calinfo.api.common.mocks;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;


@Getter
@Setter
public class MockDtoInerContrainteViolation {

    @NotNull
    private String prop1;
}
