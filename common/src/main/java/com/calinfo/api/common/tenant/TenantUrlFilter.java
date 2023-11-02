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
import com.calinfo.api.common.domain.DomainResolver;
import com.calinfo.api.common.security.keycloak.RealmContext;
import com.calinfo.api.common.security.keycloak.RealmResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URL;

@Component
@Order(TenantUrlFilter.ORDER_FILTER)
public class TenantUrlFilter extends OncePerRequestFilter {

    public static final int ORDER_FILTER = 1000;

    private final DomainResolver domainResolver;
    private final RealmResolver realmResolver;

    public TenantUrlFilter(@Autowired(required = false) DomainResolver domainResolver,
                           @Autowired(required = false) RealmResolver realmResolver){
        this.domainResolver = domainResolver;
        this.realmResolver = realmResolver;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String oldDomain = DomainContext.getDomain();
        String oldRealm = RealmContext.getRealm();
        try {

            if (domainResolver != null) {
                DomainContext.setDomain(domainResolver.getDomain(httpServletRequest));
            }

            if (realmResolver != null) {
                RealmContext.setRealm(realmResolver.getRealm(httpServletRequest));
            }

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
        finally {
            DomainContext.setDomain(oldDomain);
            RealmContext.setRealm(oldRealm);
        }
    }
}
