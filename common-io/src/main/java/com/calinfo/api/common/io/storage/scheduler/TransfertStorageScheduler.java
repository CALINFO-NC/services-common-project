package com.calinfo.api.common.io.storage.scheduler;

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

import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;
import com.calinfo.api.common.io.storage.service.BinaryDataDomainService;
import com.calinfo.api.common.io.storage.service.BinaryDataSchedulerService;
import com.calinfo.api.common.task.TaskParam;
import com.calinfo.api.common.task.TaskRunner;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@Transactional(propagation = Propagation.NEVER)
@ConditionalOnProperty(prefix = "common-io.storage.scheduler", name = "enabled", havingValue = "true")
public class TransfertStorageScheduler {

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
    @SchedulerLock(name = "TransfertStorageScheduler_runTransfertBinaryData", lockAtMostFor = "PT1H")
    public void runTransfertBinaryData(){


        if (binaryDataDomainService != null){
            binaryDataDomainService.list().stream().forEach(this::runTransfertBinaryDataWithDomainParameter);
        }

        // On effectue le transfert sans domain
        runTransfertBinaryDataWithDomainParameter(null);
    }

    private void runTransfertBinaryDataWithDomainParameter(String domain){

        taskRunner.run(TaskParam.builder()
                .domain(domain)
                .build(), () -> {

            binaryDataSchedulerService.transfert(domain);

            return Optional.empty();
        });
    }
}
