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
import com.calinfo.api.common.utils.MiscUtils;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.HashMap;
import java.util.Map;

class KeycloakClientRegistrationRepository implements ClientRegistrationRepository {

    private Map<String, ClientRegistration> cache = new HashMap<>();
    private final KeycloakProperties keycloakProperties;

    public KeycloakClientRegistrationRepository(KeycloakProperties keycloakProperties){
        this.keycloakProperties = keycloakProperties;
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {

        return cache.computeIfAbsent(registrationId, k -> ClientRegistrations
                    .fromIssuerLocation(String.format("%s/realms/%s", MiscUtils.formatEndUrl(keycloakProperties.getBaseUrl()), registrationId))
                    .clientId(keycloakProperties.getClientId())
                    .scope("openid")
                    .userNameAttributeName(keycloakProperties.getPrincipalClaimName())
                    .registrationId(registrationId)
                    .authorizationGrantType(new AuthorizationGrantType("authorization_code"))
                    .build());
    }
}
