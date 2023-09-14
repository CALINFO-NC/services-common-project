package com.calinfo.api.common.io.storage.service;

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
import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;
import com.calinfo.api.common.io.storage.service.impl.IdQueue;
import com.calinfo.api.common.io.storage.service.impl.IdQueueManager;
import com.calinfo.api.common.tenant.DomainContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@Transactional(propagation = Propagation.NEVER)
@ConditionalOnProperty(prefix = "common-io.storage.scheduler", name = "enabled", havingValue = "true")
public class BinaryDataSchedulerService {

    Lock locker = new ReentrantLock();

    @Autowired
    private IdQueueManager idQueueManager;

    @Autowired
    private BinaryDataClientService binaryDataService;

    @Autowired
    private BinaryDataConnector binaryDataConnector;


    @Async("binaryDataASyncOperation")
    public void transfert(String domain){

        String oldDomain = DomainContext.getDomain();
        try{
            DomainContext.setDomain(domain);
            createDomainIfNecessary(domain);

            IdQueue idQueue = idQueueManager.getIdQueue(domain); // FIFO concurent à valeur unique
            int count = idQueue.add(binaryDataService.listId()); // Nombre d'élément réellement rajouté

            // On passe par un count afin de laissé au autre thread la chance de traiter des ids
            // afin de laisser la chance à tous les thread de traité des ids indépendament des domaines
            int index = 0;
            String id = idQueue.get();
            while (id != null && index < count){

                try {
                    // On supprime le fichier existant s'il existe
                    Future<Boolean> future = binaryDataConnector.delete(DomainContext.getDomain(), id);

                    if (Boolean.TRUE.equals(future.get())){
                        log.info(String.format("Fichier dont l'id est '%s' du domaine '%s' a été supprimé", id, domain));
                    }

                    // On effectue le transfert
                    transfertFromId(id);


                } catch (IOException | InterruptedException | ExecutionException e) {
                    log.error(e.getMessage(), e);
                }

                id = idQueue.get();
                index++;
            }
        }
        finally {
            setDefaultDomain(oldDomain);
        }

    }

    private void createDomainIfNecessary(String domain){

        try {
            if (!binaryDataConnector.isSpaceExist(domain)){

                locker.lock();
                try {

                    if (!binaryDataConnector.isSpaceExist(domain)){
                        binaryDataConnector.createSpace(domain);
                    }
                }
                finally {
                    locker.unlock();
                }
            }
        }
        catch (IOException e){
            throw new ApplicationErrorException(e);
        }

    }


    private void transfertFromId(String binaryDataId) throws IOException {

        try(TransfertData trData = binaryDataService.startTransfert(binaryDataId)){


            InputStream in = trData.getInputStream();

            binaryDataConnector.upload(DomainContext.getDomain(), binaryDataId, in);

            closeTransfert(binaryDataId, trData.getVersion(), true);

        } catch (IOException e) {

            closeTransfert(binaryDataId, null, false);

            throw e;
        }
    }

    private void setDefaultDomain(String defaultDomain){
        try {
            DomainContext.setDomain(defaultDomain);
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    private void closeTransfert(String binaryDataId, Long version, boolean success){

        try{

            binaryDataService.finalizeTransfert(binaryDataId, version, success);

        }catch (RuntimeException e){
            log.debug(e.getMessage(), e);
            log.warn(e.getMessage());
        }
    }

}
