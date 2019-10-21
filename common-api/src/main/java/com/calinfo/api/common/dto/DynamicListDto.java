package com.calinfo.api.common.dto;

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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by dalexis on 18/11/2017.
 */
@NoArgsConstructor
@Getter
@Setter
public class DynamicListDto<R extends Serializable> implements Dto, MessageInfoAndWarningInterface {


    @Size(min = 0, max = 0)
    private List<String> listInfoMessages = new ArrayList<>();

    @Size(min = 0, max = 0)
    private List<String> listWarningMessages = new ArrayList<>();

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
