package com.calinfo.api.common.io.storage.scheduler;

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
import com.calinfo.api.common.io.storage.service.BinaryDataDomainService;
import com.calinfo.api.common.io.storage.service.BinaryDataSchedulerService;
import com.calinfo.api.common.task.TaskException;
import com.calinfo.api.common.task.TaskRunner;
import com.calinfo.api.common.tenant.DomainContext;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@Transactional(propagation = Propagation.NEVER)
@ConditionalOnProperty(prefix = "common-io.storage.scheduler", name = "enabled", havingValue = "true")
public class TransfertStorageScheduler {

    private static final Logger log = LoggerFactory.getLogger(TransfertStorageScheduler.class);

    @Autowired
    private BinaryDataSchedulerService binaryDataSchedulerService;

    @Autowired(required = false)
    private BinaryDataDomainService binaryDataDomainService;

    @Autowired
    private TaskRunner taskRunner;

    @Autowired
    private BinaryDataConnector binaryDataConnector;

    /**
     * Ce scheduler à pour objectif de trasnférer l'ensemble des données binaire en base
     * pour les transférer sur un autre support
     */
    @Scheduled(fixedDelayString = "${common-io.storage.scheduler.delay}")
    @SchedulerLock(name = "TransfertStorageScheduler_runTransfertBinaryData", lockAtMostFor = 1 * 60 * 1000)
    public void runTransfertBinaryData(){


        if (binaryDataDomainService != null){
            binaryDataDomainService.list().stream().forEach(this::runTransfertBinaryDataWithDomainParameter);
        }

        runTransfertBinaryDataWithDomainParameter(null);
    }

    private void runTransfertBinaryDataWithDomainParameter(String domain){

        try {
            taskRunner.run(domain, () -> {

                // Récupérer la liste des objets à transférer
                List<String> lstId = binaryDataSchedulerService.listId();

                for (String id: lstId){

                    // On supprime le fichier existant s'il existe
                    try {
                        binaryDataConnector.delete(DomainContext.getDomain(), id);
                    } catch (IOException e) {
                        throw new TaskException(e);
                    }

                    // On effectue le transfert
                    binaryDataSchedulerService.transfert(domain, id);
                }

                return Optional.empty();
            });
        } catch (TaskException e) {
            log.error(e.getMessage(), e);
        }
    }
}
