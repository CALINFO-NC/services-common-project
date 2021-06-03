package com.calinfo.api.common.spring;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
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

    private Pageable pageable;


    /**
     * Constructeur par défaut.
     *
     * @param ci Information de chargement
     */
    public PageInfoPageRequest(PageInfoDto ci) {
        this(ci, Sort.unsorted());
    }

    private PageInfoPageRequest(Pageable pageable) {
        this.pageable = pageable;
    }

    public PageInfoPageRequest(PageInfoDto ci, Sort sort) {

        int limit = Math.min(ci.getLimit() == null ? maxLimit : ci.getLimit(), maxLimit);
        pageable = PageRequest.of(ci.getPage(), limit, sort);
    }

    @Override
    public int getPageNumber() {
        return pageable.getPageNumber();
    }

    @Override
    public int getPageSize() {
        return pageable.getPageSize();
    }

    @Override
    public long getOffset() {
        return pageable.getOffset();
    }

    @Override
    public Sort getSort() {
        return pageable.getSort();
    }

    @Override
    public Pageable next() {
        return new PageInfoPageRequest(pageable.next());
    }

    @Override
    public Pageable previousOrFirst() {
        return new PageInfoPageRequest(pageable.previousOrFirst());
    }

    @Override
    public Pageable first() {
        return new PageInfoPageRequest(pageable.first());
    }

    @Override
    public boolean hasPrevious() {
        return pageable.hasPrevious();
    }
}

