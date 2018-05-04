package com.calinfo.api.common.dto;

import com.calinfo.api.common.type.TypeAttribut;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AttributDto {


    private String name;

    private TypeAttribut type;

    private List<String> listMessages = new ArrayList<>();
}
