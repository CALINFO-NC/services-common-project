package com.calinfo.api.common.service;

/*-
 * #%L
 * common-api
 * %%
 * Copyright (C) 2019 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
