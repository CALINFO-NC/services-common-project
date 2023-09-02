package com.calinfo.api.common.security;

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


import com.calinfo.api.common.tenant.DomainResolver;
import com.calinfo.api.common.tenant.Request;
import com.calinfo.api.common.utils.MiscUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.http.HttpMethod;

import java.net.URL;

@Slf4j
@RequiredArgsConstructor
public class CommonKeycloakConfigResolver implements KeycloakConfigResolver {

    private final AdapterConfig adapterConfig;
    private final DomainResolver domainResolver;

    @SneakyThrows
    @Override
    public KeycloakDeployment resolve(HttpFacade.Request request) {

        Request req = new Request();
        req.setUrl(new URL(request.getURI()));
        req.setMethod(HttpMethod.valueOf(request.getMethod()));
        req.setHeaders(request::getHeader);
        req.setParameters(request::getFirstParam);

        String realm = domainResolver.getDomain(req);

        ObjectMapper mapper = MiscUtils.getObjectMapper();
        String str = mapper.writeValueAsString(adapterConfig);
        AdapterConfig copyAdapterConfig = mapper.readValue(str, AdapterConfig.class);

        if (StringUtils.isBlank(adapterConfig.getRealm())) {
            copyAdapterConfig.setRealm(realm);
        }

        if (StringUtils.isBlank(copyAdapterConfig.getRealm())){
            return new KeycloakDeployment();
        }
        else {
            return KeycloakDeploymentBuilder.build(copyAdapterConfig);
        }
    }

}
