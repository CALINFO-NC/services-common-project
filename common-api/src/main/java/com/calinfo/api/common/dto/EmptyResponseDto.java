package com.calinfo.api.common.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 18/11/2017.
 */
@Getter
@Setter
public class EmptyResponseDto implements Dto, MessageInfoAndWarningInterface {

    @Size(min = 0, max = 0)
    private List<String> listInfoMessages = new ArrayList<>();

    @Size(min = 0, max = 0)
    private List<String> listWarningMessages = new ArrayList<>();
}
