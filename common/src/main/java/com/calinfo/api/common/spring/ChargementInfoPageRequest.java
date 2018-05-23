package com.calinfo.api.common.spring;

import com.calinfo.api.common.dto.ChargementInfoDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
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

    /**
     * Nombre total d'éléments à charger
     */
    private int limit;

    /**
     * Index de début de chargement
     */
    private int offset;
    private Sort sort;

    /**
     * Constructeur par défaut.
     *
     * @param ci Information de chargement
     */
    public ChargementInfoPageRequest(ChargementInfoDto ci) {
        this(ci.getStart(), ci.getLimit());
    }

    /**
     * Constructeur par défaut avec sort.
     *
     * @param ci   Information de chargement
     * @param sort
     */
    public ChargementInfoPageRequest(ChargementInfoDto ci, Sort sort) {
        this(ci.getStart(), ci.getLimit());
        this.sort = sort;
    }

    /**
     * Constructeur interne
     *
     * @param offset Début de chargement
     * @param limit  Nombre d'éléments à charger
     */
    private ChargementInfoPageRequest(int offset, Integer limit) {

        if (limit == null || limit.intValue() > getMaxLimit()){
            this.limit = getMaxLimit();
        }
        else {
            this.limit = limit;
        }
        this.offset = offset;
    }

    @Override
    public int getPageNumber() {
        return 1;
    }

    @Override
    public int getPageSize() {
        return this.limit;
    }

    @Override
    public int getOffset() {
        return this.offset;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    @Override
    public Pageable next() {
        return new ChargementInfoPageRequest(getOffset() + getPageSize(), getPageSize());
    }

    public ChargementInfoPageRequest previous() {
        return hasPrevious() ? new ChargementInfoPageRequest(getOffset() - getPageSize(), getPageSize()) : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new ChargementInfoPageRequest(0, getPageSize());
    }

    @Override
    public boolean hasPrevious() {
        return this.offset >= this.limit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("limit", this.limit).append("offset", this.offset).toString();
    }
}

