package com.calinfo.api.common.task;


import java.util.Optional;

@FunctionalInterface
public interface Task<T> {
    Optional<T> run() throws TaskException;
}
