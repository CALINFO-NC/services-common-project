package com.calinfo.api.common;

import java.util.List;

/**
 * Created by dalexis on 31/12/2017.
 */
public interface ErrorMessageFieldConvertor {

    String convert(String fieldName, List<String> erros);
}
