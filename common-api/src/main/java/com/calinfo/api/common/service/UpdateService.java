package com.calinfo.api.common.service;

import com.calinfo.api.common.resource.Resource;

/**
 * Created by dalexis on 04/04/2018.
 *
 */
public interface UpdateService<T extends Resource, I> extends UpdateProjectionService<T, T, I> {

    T update(I id, T resource);
}
