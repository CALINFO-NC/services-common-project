package com.calinfo.api.common.spring;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2023 CALINFO
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

import com.calinfo.api.common.dto.PageInfoDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Classe permettant de traiter un {@link PageInfoDto} comme un objet Pageable de spring
 */
public class PageInfoPageRequest implements Pageable {

    /**
     * Limit max à utiliser dans les chargements
     */
    @Getter
    @Setter
    private static int maxLimit = 100;

    @Delegate
    private Pageable pageable;


    /**
     * Constructeur par défaut.
     *
     * @param ci Information de chargement
     */
    public PageInfoPageRequest(PageInfoDto ci) {
        this(ci, Sort.unsorted());
    }

    public PageInfoPageRequest(Pageable pageable) {
        this.pageable = pageable;
    }

    public PageInfoPageRequest(PageInfoDto ci, Sort sort) {

        int limit = Math.min(ci.getLimit() == null ? maxLimit : ci.getLimit(), maxLimit);
        pageable = PageRequest.of(ci.getPage(), limit, sort);
    }
}

