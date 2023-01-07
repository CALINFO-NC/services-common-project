package com.calinfo.api.common;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2023 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dalexis on 22/11/2017.
 */
public class FieldErrorStructure extends HashMap<String, List<MessageStructure>> {

    public void put(String key, MessageStructure firstValue, MessageStructure... values){

        List<MessageStructure> lst = this.computeIfAbsent(key, k ->  new ArrayList<>());

        lst.add(firstValue);
        for(MessageStructure item : values)
            lst.add(item);
    }
}
