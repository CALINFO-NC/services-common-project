package com.calinfo.api.common.service;

import com.calinfo.api.common.resource.Resource;

/**
 * Created by dalexis on 04/04/2018.
 *
 * @deprecated utiliser {@link DistinctUpdateService} qui permet la distinction entre le paramètre d'entrée et de retour
 */
@Deprecated
public interface UpdateService<T extends Resource, I> extends DistinctUpdateService<T, T, I> {

    T update(I id, T resource);
}
