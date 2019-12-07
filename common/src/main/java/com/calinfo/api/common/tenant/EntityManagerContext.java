package com.calinfo.api.common.tenant;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 CALINFO
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


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TransactionRequiredException;

/**
 * Created by dalexis on 29/05/2018.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityManagerContext {

    private static final Logger log = LoggerFactory.getLogger(EntityManagerContext.class);

    private static ThreadLocal<EntityManager> currentEm = new ThreadLocal<>();

    /**
     * Cette méthode retourne une instance de EntityManager et tente de la joindre à la transactino courante.
     * Si aucune transaction courrante existe, alors une log de type info est levée et aucune exception n'est lancée
     *
     * @param emf Fabrique d'EntityManager
     * @return EntityManager issue de la fabrique
     */
    public static EntityManager smartGet(EntityManagerFactory emf) {

        EntityManager em = currentEm.get();

        if (em == null || !em.isOpen()){
            em = emf.createEntityManager();
            currentEm.set(em);
        }

        if (!em.isJoinedToTransaction()){
            try {
                em.joinTransaction();
            }
            catch(TransactionRequiredException e){
                log.info(e.getMessage(), e);
            }
        }

        return em;
    }
}
