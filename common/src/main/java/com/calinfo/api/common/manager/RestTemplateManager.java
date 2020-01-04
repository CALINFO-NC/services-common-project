package com.calinfo.api.common.manager;

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

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dalexis on 05/01/2018.
 */

@Component
public class RestTemplateManager {

    private ReentrantLock locker = new ReentrantLock();
    private RestTemplate restTemplate = null;

    public RestTemplate getRestTemplate(){

        if (restTemplate == null){

            locker.lock();

            try {

                if (restTemplate == null) {
                    restTemplate = new RestTemplate();
                }
            }
            finally {
                locker.unlock();
            }
        }

        return restTemplate;
    }
}
