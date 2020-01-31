package com.calinfo.api.common.tenant;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class DomainAwarePoolExecutor extends ThreadPoolTaskExecutor {

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(new DomainAwareCallable(task, DomainContext.getDomain()));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return super.submitListenable(new DomainAwareCallable(task, DomainContext.getDomain()));
    }
}
