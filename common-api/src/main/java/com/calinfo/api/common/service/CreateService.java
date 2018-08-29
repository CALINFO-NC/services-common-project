package com.calinfo.api.common.service;

import com.calinfo.api.common.resource.Resource;

/**
 * Created by dalexis on 04/04/2018.
 *
 * @deprecated utiliser {@link DistinctCreateService} qui permet la distinction entre le paramètre d'entrée et de retour
 */
@Deprecated
public interface CreateService<T extends Resource> extends DistinctCreateService<T, T> {

    T create(T resource);
}
