package com.calinfo.api.common.dto;

/*-
 * #%L
 * common-api
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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by dalexis on 18/11/2017.
 */
@NoArgsConstructor
@Data
@Schema(name = "DynamicList")
public class DynamicListDto<R extends Serializable> extends DefaultMessageInfoAndWarning implements Dto, MessageInfoAndWarningInterface {


    private List<R> data = new ArrayList<>();

    /**
     * Correspond soit au nombre d'élément, soit au nombre de page disponnible
     */
    private long count;

    public <E> DynamicListDto(Function<E, R> function, Page<E> page){

        count = page.getTotalElements();
        for (E e : page.getContent()){
            R r = function.apply(e);
            data.add(r);
        }
    }

    public DynamicListDto(List<R> globalList, int start, int limit){

        int first = start * limit;

        if (first < globalList.size()){

            int offset = first + limit - 1;

            int max = Math.min(offset, globalList.size() - 1);

            for (int i = first; i <= max; i++){
                data.add(globalList.get(i));
            }
        }

        count = globalList.size();
    }
}
