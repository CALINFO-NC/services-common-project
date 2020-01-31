package com.calinfo.api.common.tenant;

import java.util.concurrent.Callable;

public class DomainAwareCallable<T> implements Callable<T> {
    private Callable<T> task;
    private String domain;

    public DomainAwareCallable(Callable<T> task, String domain) {
        this.task = task;
        this.domain = domain;
    }

    @Override
    public T call() throws Exception {
        String oldDomain = DomainContext.getDomain();
        if (domain != null) {
            DomainContext.setDomain(domain);
        }

        try {
            return task.call();
        } finally {
            DomainContext.setDomain(oldDomain);
        }
    }
}
