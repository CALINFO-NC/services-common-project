package com.calinfo.api.common.service;

import com.calinfo.api.common.resource.Resource;

/**
 *
 * La création prend en entrée un "{@link Resource}" et renvoi une "{@link Resource}"
 *
 * @param <I> type du paramètre d'entrée de la fonction {@link #create(Resource)}
 * @param <O> type du paramètre de sortie de la fonction {@link #create(Resource)}
 */
public interface DistinctCreateService<I extends Resource, O extends Resource> {

    O create(I i);
}
