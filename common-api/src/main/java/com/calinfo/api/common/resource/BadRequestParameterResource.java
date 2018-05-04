package com.calinfo.api.common.resource;

import com.calinfo.api.common.dto.AttributDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 20/11/2017.
 */
@Getter
@Setter
public class BadRequestParameterResource {

    private List<AttributDto> listErrorMessages = new ArrayList<>();
}
