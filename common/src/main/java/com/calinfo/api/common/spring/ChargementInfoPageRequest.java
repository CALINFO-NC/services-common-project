package com.calinfo.api.common.spring;

import com.calinfo.api.common.dto.ChargementInfoDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Classe permettant de traiter un {@link ChargementInfoDto} comme un objet Pageable de spring
 */
public class ChargementInfoPageRequest implements Pageable {

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
    public ChargementInfoPageRequest(ChargementInfoDto ci) {
        this(ci, Sort.unsorted());
    }

    private ChargementInfoPageRequest(Pageable pageable) {
        this.pageable = pageable;
    }

    public ChargementInfoPageRequest(ChargementInfoDto ci, Sort sort) {

        int limit = Math.min(ci.getLimit() == null ? maxLimit : ci.getLimit(), maxLimit);
        pageable = PageRequest.of(ci.getStart(), limit, sort);
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
        return new ChargementInfoPageRequest(pageable.next());
    }

    @Override
    public Pageable previousOrFirst() {
        return new ChargementInfoPageRequest(pageable.previousOrFirst());
    }

    @Override
    public Pageable first() {
        return new ChargementInfoPageRequest(pageable.first());
    }

    @Override
    public boolean hasPrevious() {
        return pageable.hasPrevious();
    }
}

