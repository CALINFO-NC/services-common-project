package com.calinfo.api.common.io.storage.mock;

import com.calinfo.api.common.io.storage.service.BinaryDataDomainService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MockBinaryDataDomainService implements BinaryDataDomainService {

    @Getter
    private List<String> data = new ArrayList<>();

    @Override
    public List<String> list() {
        return data;
    }
}
