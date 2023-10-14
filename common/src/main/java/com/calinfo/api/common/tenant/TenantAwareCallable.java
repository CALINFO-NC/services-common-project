package com.calinfo.api.common.tenant;

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
import com.calinfo.api.common.security.keycloak.RealmContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class TenantAwareCallable<T> implements Callable<T> {
    private Callable<T> task;
    private String domain;
    private String realm;

    public TenantAwareCallable(Callable<T> task, String domain, String realm) {
        this.task = task;
        this.domain = domain;
        this.realm = realm;
    }

    @Override
    public T call() throws Exception {
        String oldDomain = DomainContext.getDomain();
        String oldRealm = RealmContext.getRealm();

        try {
            DomainContext.setDomain(domain);
            RealmContext.setRealm(realm);

            return task.call();
        } finally {
            setDomain(oldDomain);
            setRealm(oldRealm);
        }
    }

    private void setDomain(String domain){
        try {
            DomainContext.setDomain(domain);
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    private void setRealm(String realm){
        try {
            RealmContext.setRealm(realm);
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }
}
