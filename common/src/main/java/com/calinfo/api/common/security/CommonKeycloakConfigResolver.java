package com.calinfo.api.common.security;

/*-
 * #%L
 * common
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


import com.calinfo.api.common.utils.MiscUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.representations.adapters.config.AdapterConfig;

@Slf4j
@RequiredArgsConstructor
public class CommonKeycloakConfigResolver implements KeycloakConfigResolver {

    private final AdapterConfig adapterConfig;

    @SneakyThrows
    @Override
    public KeycloakDeployment resolve(HttpFacade.Request request) {


        String realm = request.getHeader(CommonSecurityUrlFilter.HEADER_DOMAIN);

        ObjectMapper mapper = MiscUtils.getObjectMapper();
        String str = mapper.writeValueAsString(adapterConfig);
        AdapterConfig copyAdapterConfig = mapper.readValue(str, AdapterConfig.class);
        copyAdapterConfig.setRealm(realm);

        return KeycloakDeploymentBuilder.build(copyAdapterConfig);
    }

}
