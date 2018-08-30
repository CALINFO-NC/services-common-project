package com.calinfo.api.common.service;

import com.calinfo.api.common.resource.Resource;

/**
 * Created by dalexis on 04/04/2018.
 */
public interface CreateService<T extends Resource> extends CreateProjectionService<T, T> {

    T create(T resource);
}
