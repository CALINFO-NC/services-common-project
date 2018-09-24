package com.calinfo.api.common.response;

import com.calinfo.api.common.dto.AttributDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 20/11/2017.
 */
@Getter
@Setter
public class BadRequestParameterResponse implements Serializable {

    private List<AttributDto> listErrorMessages = new ArrayList<>();
}
