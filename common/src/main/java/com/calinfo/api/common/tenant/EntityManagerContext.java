package com.calinfo.api.common.tenant;

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


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TransactionRequiredException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dalexis on 29/05/2018.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityManagerContext {

    private static final Logger log = LoggerFactory.getLogger(EntityManagerContext.class);

    private static ThreadLocal<Map<SmartManagerFactoryKey, EntityManager>> currentEm = new ThreadLocal<>();

    private static SmartManagerFactoryKey getSmartKey(EntityManagerFactory emf){

        SmartManagerFactoryKey key = new SmartManagerFactoryKey();
        key.setEntityManagerFactory(emf);

        CurrentTenantIdentifierResolver resolver = (CurrentTenantIdentifierResolver)emf.getProperties().get(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER);
        if (resolver != null){
            key.setIdentifiantResolver(resolver.resolveCurrentTenantIdentifier());
        }

        return key;
    }

    /**
     * Cette méthode retourne une instance de EntityManager et tente de la joindre à la transactino courante.
     * Si aucune transaction courrante existe, alors une log de type info est levée et aucune exception n'est lancée
     *
     * @param emf Fabrique d'EntityManager
     * @return EntityManager issue de la fabrique
     */
    public static EntityManager smartGet(EntityManagerFactory emf) {

        SmartManagerFactoryKey key = getSmartKey(emf);

        Map<SmartManagerFactoryKey, EntityManager> map = currentEm.get();
        if (map == null){
            map = new HashMap<>();
            currentEm.set(map);
        }

        EntityManager em = map.get(key);

        if (em == null || !em.isOpen()){
            em = emf.createEntityManager();
            map.put(key, em);
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

    /**
     *
     * @param em EntityManager
     * @return true si une EntityManager à étté créé
     */
    public static boolean isEntityManagerExist(EntityManager em){

        Map<SmartManagerFactoryKey, EntityManager> map = currentEm.get();

        if (map == null){
            return false;
        }

        return map.entrySet().stream().anyMatch(i -> i.getValue().equals(em));
    }

    /**
     *
     * @param emf Fabrique d'EntityManager
     * @return true si une EntityManager à étté créé
     */
    public static boolean isEntityManagerExist(EntityManagerFactory emf){

        Map<SmartManagerFactoryKey, EntityManager> map = currentEm.get();

        if (map == null){
            return false;
        }

        return map.entrySet().stream().anyMatch(i -> i.getKey().equals(getSmartKey(emf)));
    }

    /**
     *
     * @param em Supprimer EntityManager si elle existe
     */
    public static void removeExistingEntityManager(EntityManager em){

        Map<SmartManagerFactoryKey, EntityManager> map = currentEm.get();

        if (map == null){
            return;
        }

        List<Map.Entry<SmartManagerFactoryKey, EntityManager>> lst = map.entrySet().stream().filter(i -> i.getValue().equals(em)).collect(Collectors.toList());

        if (!lst.isEmpty()){
            map.remove(lst.get(0).getKey());
        }
    }
}
