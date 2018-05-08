package com.calinfo.api.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dalexis on 22/11/2017.
 */
public class FieldError extends HashMap<String, List<MessageStructure>> {

    public void put(String key, MessageStructure firstValue, MessageStructure... values){



        List<MessageStructure> lst = this.computeIfAbsent(key, k ->  new ArrayList<>());

        lst.add(firstValue);
        for(MessageStructure item : values)
            lst.add(item);
    }
}
