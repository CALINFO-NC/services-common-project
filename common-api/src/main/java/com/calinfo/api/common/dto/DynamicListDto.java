package com.calinfo.api.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by dalexis on 18/11/2017.
 */
@NoArgsConstructor
@Getter
@Setter
public class DynamicListDto<R extends Dto> implements Dto {


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
}
