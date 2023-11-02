package com.calinfo.api.common.task;

/*-
 * #%L
 * common
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

import com.calinfo.api.common.domain.DomainContext;
import com.calinfo.api.common.ex.ApplicationErrorException;
import com.calinfo.api.common.security.keycloak.RealmContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Slf4j
@Component
public class ContextRunner {

    public <T> T run (ContextParam contextParam, Supplier<T> task) {

        String actualDomain = DomainContext.getDomain();
        String actualRealm = RealmContext.getRealm();
        Authentication actualAuth = SecurityContextHolder.getContext().getAuthentication();

        String domainName = contextParam.getDomain();
        String realmName = contextParam.getRealm();

        try {
            // Mettre en place le domain
            DomainContext.setDomain(domainName);

            // Mettre en place le realm
            RealmContext.setRealm(realmName);
            SecurityContextHolder.getContext().setAuthentication(contextParam.getAuthentication());

            // Exécuter la tâche
            return task.get();

        }
        finally {

            // Re mettre en place l'ancien domain
            boolean setOk = setDomainContext(actualDomain);

            // Re mettre en place l'ancien realm
            setOk = setOk && setRealmContext(actualRealm);

            // Re mettre en place l'ancien,e authentification
            setOk = setOk && setAuth(actualAuth);

            if (!setOk){
                throw new ApplicationErrorException("L'exécution du task runner ne s'est pas bien déroulé. Voir les autres exceptions");
            }
        }
    }

    private boolean setRealmContext(String realmContext){

        try{
            RealmContext.setRealm(realmContext);
            return true;
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private boolean setDomainContext(String domainContext){

        try{
            DomainContext.setDomain(domainContext);
            return true;
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private boolean setAuth(Authentication auth){

        try{
            SecurityContextHolder.getContext().setAuthentication(auth);
            return true;
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
