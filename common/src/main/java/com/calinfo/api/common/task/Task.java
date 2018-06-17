package com.calinfo.api.common.task;


@FunctionalInterface
public interface Task {
    void run() throws TaskException;
}
