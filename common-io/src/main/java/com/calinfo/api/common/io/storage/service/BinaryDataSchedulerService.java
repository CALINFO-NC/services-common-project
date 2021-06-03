package com.calinfo.api.common.io.storage.service;

/*-
 * #%L
 * common-io
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
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

import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;
import com.calinfo.api.common.tenant.DomainContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Transactional(propagation = Propagation.NEVER)
@ConditionalOnProperty(prefix = "common-io.storage.scheduler", name = "enabled", havingValue = "true")
public class BinaryDataSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(BinaryDataSchedulerService.class);

    @Autowired
    private BinaryDataClientService binaryDataService;

    @Autowired
    private BinaryDataConnector binaryDataConnector;

    public List<String> listId(){
        return binaryDataService.listId();
    }


    @Async("binaryDataASyncOperation")
    public void transfert(String domainName, String binaryDataId){

        String oldDomain = DomainContext.getDomain(); // On est dans une méthode asynchrone, on a donc pas le domaine qui est ThreadSafe
        DomainContext.setDomain(domainName);

        try(TransfertData trData = binaryDataService.startTransfert(binaryDataId)){

            InputStream in = trData.getInputStream();

            binaryDataConnector.upload(DomainContext.getDomain(), binaryDataId, in);

            closeTransfert(binaryDataId, trData.getVersion(), true);
        } catch (IOException e) {

            closeTransfert(binaryDataId, null, false);

            log.error(e.getMessage(), e);
        }
        finally {
            DomainContext.setDomain(oldDomain);
        }
    }

    private void closeTransfert(String binaryDataId, Long version, boolean success){

        try{

            binaryDataService.finalizeTransfert(binaryDataId, version, success);

        }catch (RuntimeException e){
            log.warn(e.getMessage(), e);
        }
    }

}
