package com.calinfo.api.common.resource;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BadResponseResource {

    private Map<String, List<String>> mapErrorMessagesFields = new HashMap<>();

    private List<String> listErrorMessages = new ArrayList<>();
}
