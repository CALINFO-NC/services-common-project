package com.calinfo.api.common.dto;

import com.calinfo.api.common.resource.Resource;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 18/11/2017.
 */
@Getter
@Setter
public class DynamicListDto<R extends Resource> implements Dto {

    private List<R> data = new ArrayList<>();

    /**
     * Correspond soit au nombre d'élément, soit au nombre de page disponnible
     */
    private long count;
}
