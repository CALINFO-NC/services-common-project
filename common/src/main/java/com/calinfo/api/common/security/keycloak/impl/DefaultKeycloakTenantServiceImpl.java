package com.calinfo.api.common.security.keycloak.impl;

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

import com.calinfo.api.common.security.keycloak.KeycloakProperties;
import com.calinfo.api.common.security.keycloak.KeycloakTenant;
import com.calinfo.api.common.security.keycloak.KeycloakTenantService;
import com.calinfo.api.common.security.keycloak.KeycloakUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@ConditionalOnProperty(prefix = "common.configuration.keycloak", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Service
public class DefaultKeycloakTenantServiceImpl implements KeycloakTenantService {

    private final KeycloakProperties keycloakProperties;

    @Override
    public String extractTenantIdFromIssuerUrl(String issuerUrl){
        return KeycloakUtils.getTenantIdFromIssuerUrl(keycloakProperties.getBaseUrl(), issuerUrl);
    }

    @Override
    public Optional<KeycloakTenant> getByIssuer(String tenantId){

        String baseUrl = keycloakProperties.getBaseUrl();
        if (baseUrl.endsWith("/")){
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        KeycloakTenant keycloakTenant = new KeycloakTenant();
        keycloakTenant.setIssuer(String.format("%s/realms/%s", baseUrl, tenantId));
        keycloakTenant.setJwkSetUrl(String.format("%s/realms/%s/protocol/openid-connect/certs", baseUrl, tenantId));

        return Optional.of(keycloakTenant);
    }
}
