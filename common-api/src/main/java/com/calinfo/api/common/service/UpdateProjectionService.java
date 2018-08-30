package com.calinfo.api.common.service;

import com.calinfo.api.common.dto.Dto;
import com.calinfo.api.common.resource.Resource;

/**
 * La mise à jour prend en entrée un "{@link Dto}" et renvoi une "{@link Resource}"
 *
 * @param <I> type du paramètre d'entrée de la fonction {@link #update(Object, Resource)}
 * @param <O> type du paramètre de sortie de la fonction {@link #update(Object, Resource)}
 * @param <ID> identifiant de la resource
 */
public interface UpdateProjectionService<I extends Resource, O extends Resource, ID> {

    O update(ID id, I resource);
}
