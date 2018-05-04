package com.calinfo.api.common.mocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.Validator;

@Service
@Validated
public class MockServiceConstrainteVilationService {


    @Autowired
    private Validator validator;

    public void validation(@Valid MockDtoContrainteViolation prm){

    }
}
