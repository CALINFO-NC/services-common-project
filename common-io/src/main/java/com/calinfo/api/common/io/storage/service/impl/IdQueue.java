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

import com.calinfo.api.common.ex.ApplicationErrorException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class IdQueue {

    private Lock locker = new ReentrantLock();
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public int add(List<String> ids) {

        AtomicInteger count = new AtomicInteger();
        ids.forEach(id -> {
            if (add(id)){
                count.incrementAndGet();
            }
        });

        return count.get();
    }

    private boolean add(String id) {

        locker.lock();
        try{
            if (!queue.contains(id)){
                queue.put(id);
                return true;
            }
            else{
                log.trace(String.format("Id '%s' déjà présent dans la queue", id));
            }
        }
        catch (InterruptedException e){
            throw new ApplicationErrorException(e);
        }
        finally {
            locker.unlock();
        }

        return false;
    }

    public String get(){
        return queue.poll();
    }

    public int size(){
        return queue.size();
    }
}
