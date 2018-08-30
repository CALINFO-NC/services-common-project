package com.calinfo.api.common.service;

import com.calinfo.api.common.dto.Dto;
import com.calinfo.api.common.resource.Resource;

/**
 * La mise à jour prend en entrée un "{@link Dto}" et renvoi une "{@link Resource}"
 *
 * @param <I> type du paramètre d'entrée de la fonction {@link #update(Object, Dto)}
 * @param <O> type du paramètre de sortie de la fonction {@link #update(Object, Dto )}
 * @param <ID> identifiant de la resource
 */
public interface DistinctUpdateService<I extends Resource, O extends Dto, ID> {

    O update(ID id, I dto);
}
