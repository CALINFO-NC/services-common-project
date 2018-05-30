package com.calinfo.api.common.scheduler;


@FunctionalInterface
public interface Task {
    void run() throws Exception;
}
