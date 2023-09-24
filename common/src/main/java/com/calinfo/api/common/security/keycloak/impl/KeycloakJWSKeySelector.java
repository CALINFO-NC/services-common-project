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

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;

import java.net.URL;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class KeycloakJWSKeySelector  implements JWTClaimsSetAwareJWSKeySelector<SecurityContext> {
    private final KeycloakTenantService keycloakTenantService;
    private final Map<String, JWSKeySelector<SecurityContext>> selectors = new ConcurrentHashMap<>();

    public KeycloakJWSKeySelector(KeycloakTenantService keycloakTenantService) {
        this.keycloakTenantService = keycloakTenantService;
    }

    @Override
    public List<? extends Key> selectKeys(JWSHeader jwsHeader,
                                          JWTClaimsSet jwtClaimsSet,
                                          SecurityContext securityContext) throws KeySourceException {
        return selectors.computeIfAbsent(toTenant(jwtClaimsSet), this::fromTenant)
                .selectJWSKeys(jwsHeader, securityContext);
    }

    protected String toTenant(JWTClaimsSet claimSet) {
        String issuerUrl = claimSet.getIssuer();
        return keycloakTenantService.extractTenantIdFromIssuerUrl(issuerUrl);
    }

    protected JWSKeySelector<SecurityContext> fromTenant(String issuer) {
        return keycloakTenantService.getByIssuer(issuer)
                .map(KeycloakTenant::getJwkSetUrl)
                .map(this::fromUri)
                .orElseThrow(() -> new IllegalArgumentException("Unknown tenant"));
    }

    protected JWSKeySelector<SecurityContext> fromUri(String uri) {
        try {
            return JWSAlgorithmFamilyJWSKeySelector.fromJWKSetURL(new URL(uri));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
