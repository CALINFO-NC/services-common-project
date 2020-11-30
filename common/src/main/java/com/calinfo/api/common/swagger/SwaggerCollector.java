package com.calinfo.api.common.swagger;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2020 CALINFO
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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Deprecated(since = "1.2.0", forRemoval = true)
@Component
@ConditionalOnProperty(prefix = "common.deprecated.swagger", name = "enabled", havingValue = "true")
public class SwaggerCollector {

    private List<SwaggerItemCollector> lstItemCollector = new ArrayList<>();

    public void add(SwaggerItemCollector item){
        lstItemCollector.add(item);
    }


    public List<SwaggerItemCollector> getAll(){

        return lstItemCollector;
    }
}
