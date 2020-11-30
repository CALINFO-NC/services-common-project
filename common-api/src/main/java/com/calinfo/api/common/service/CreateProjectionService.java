package com.calinfo.api.common.service;

/*-
 * #%L
 * common-api
 * %%
 * Copyright (C) 2019 - 2020 CALINFO
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

/**
 *
 * La création prend en entrée un "{@link com.calinfo.api.common.dto.Dto}" et renvoi une "{@link com.calinfo.api.common.dto.Dto}"
 *
 * @param <I> type du paramètre d'entrée de la fonction {@link #create(Dto)}
 * @param <O> type du paramètre de sortie de la fonction {@link #create(Dto)}
 */
public interface CreateProjectionService<I extends Dto, O extends Dto> {

    O create(I id);
}
