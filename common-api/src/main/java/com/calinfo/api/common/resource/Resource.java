package com.calinfo.api.common.resource;

import com.calinfo.api.common.dto.Dto;
import com.calinfo.api.common.dto.MessageInfoAndWarningInterface;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 18/11/2017.
 */
@Getter
@Setter
public class Resource implements Dto, MessageInfoAndWarningInterface {

    private List<String> listInfoMessages = new ArrayList<>();

    private List<String> listWarningMessages = new ArrayList<>();
}
