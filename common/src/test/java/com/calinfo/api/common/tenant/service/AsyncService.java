package com.calinfo.api.common.tenant.service;

import com.calinfo.api.common.domain.DomainContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class AsyncService {

    @Async
    public Future<String> call(){
        return CompletableFuture.completedFuture(DomainContext.getDomain());
    }
}
