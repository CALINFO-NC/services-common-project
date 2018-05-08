package com.calinfo.api.common;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ServiceErrorStructure implements Serializable {

    private FieldErrorStructure fieldsErrors = new FieldErrorStructure();

    private List<MessageStructure> globalErrors = new ArrayList<>();
}
