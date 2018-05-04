package com.calinfo.api.common.service;

import com.calinfo.api.common.resource.Resource;

/**
 * Created by dalexis on 04/04/2018.
 */
public interface DeleteService<I> {

    void delete(I id);
}
