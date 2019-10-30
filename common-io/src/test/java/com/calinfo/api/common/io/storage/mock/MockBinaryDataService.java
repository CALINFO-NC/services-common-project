package com.calinfo.api.common.io.storage.mock;

import com.calinfo.api.common.io.storage.service.BinaryDataClientService;
import com.calinfo.api.common.tenant.DomainContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MockBinaryDataService implements BinaryDataClientService {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Item {

        private String id;

        private String domain;

        private boolean finalizeSuccess = false;

        private InputStream startInputStream = null;
    }

    @Getter
    private List<Item> data = new ArrayList<>();

    @Override
    public List<String> listId() {
        return data.stream().filter(i -> (i.getDomain() == null && DomainContext.getDomain() == null) || (i.getDomain() != null && i.getDomain().equals(DomainContext.getDomain()))).map(Item::getId).collect(Collectors.toList());
    }

    @Override
    public InputStream startTransfert(String id) {
        List<Item> one = data.stream().filter(i -> i.getId().equals(id) && ((i.getDomain() == null && DomainContext.getDomain() == null) || (i.getDomain() != null && i.getDomain().equals(DomainContext.getDomain())))).collect(Collectors.toList());
        return one.isEmpty() ? null : one.get(0).getStartInputStream();
    }

    @Override
    public void finalizeTransfert(String id, boolean success) {
        List<Item> one = data.stream().filter(i -> i.getId().equals(id) && ((i.getDomain() == null && DomainContext.getDomain() == null) || (i.getDomain() != null && i.getDomain().equals(DomainContext.getDomain())))).collect(Collectors.toList());

        if (!one.isEmpty()){
            one.get(0).setFinalizeSuccess(success);
        }
    }
}
