package com.calinfo.api.common.io.storage.service.impl;

/*-
 * #%L
 * common-io
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

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdQueueManager {

    private ConcurrentHashMap<String, IdQueue> cache = new ConcurrentHashMap<>();
    private IdQueue nullDomainIdQueue = new IdQueue();

    public IdQueue getIdQueue(String domain){

        if (domain == null){
            return nullDomainIdQueue;
        }

        return cache.computeIfAbsent(domain, d -> new IdQueue());
    }
}
