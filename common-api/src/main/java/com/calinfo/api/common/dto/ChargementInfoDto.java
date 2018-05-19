package com.calinfo.api.common.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by dalexis on 18/11/2017.
 */
@Getter
@Setter
public class ChargementInfoDto implements Dto {

    private int start = 0;

    private Integer limit;

    public ChargementInfoDto(){
    }

    public ChargementInfoDto(int start){
        this.start = start;
    }

    public ChargementInfoDto(int start, Integer limit){
        this.start = start;
        this.limit = limit;
    }
}
