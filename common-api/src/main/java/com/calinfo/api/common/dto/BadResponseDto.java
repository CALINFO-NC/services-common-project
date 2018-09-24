package com.calinfo.api.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BadResponseDto implements Serializable {

    private Map<String, List<String>> mapErrorMessagesFields = new HashMap<>();

    private List<String> listErrorMessages = new ArrayList<>();
}
