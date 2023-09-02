package com.calinfo.api.common.mocks;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@Validated
public class MockServiceConstrainteVilationService {

    public void validation(@Valid MockDtoContrainteViolation prm){

    }
}
