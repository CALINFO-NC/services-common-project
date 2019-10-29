package com.calinfo.api.common.io.storage.mock;

import com.calinfo.api.common.io.storage.service.BinaryDataDomainService;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MockBinaryDataDomainService implements BinaryDataDomainService {

    @Getter
    private List<String> data = new ArrayList<>();

    @Override
    public List<String> list() {
        return data;
    }
}
