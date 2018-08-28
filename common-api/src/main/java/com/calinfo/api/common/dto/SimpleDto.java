package com.calinfo.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by dalexis on 18/11/2017.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SimpleDto<R extends Serializable> implements Dto {

    private R value;

}
