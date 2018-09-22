package com.calinfo.api.common.resource;

import com.calinfo.api.common.dto.Dto;
import com.calinfo.api.common.dto.MessageInfoAndWarningInterface;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 18/11/2017.
 */
@Getter
@Setter
public class Resource implements Dto, MessageInfoAndWarningInterface {


    @NotNull
    @Size(min = 0, max = 0)
    private List<String> listInfoMessages = new ArrayList<>();


    @NotNull
    @Size(min = 0, max = 0)
    private List<String> listWarningMessages = new ArrayList<>();
}
