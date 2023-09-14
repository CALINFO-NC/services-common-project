package com.calinfo.api.common.security.keycloak;

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

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeycloakJwtIssuerValidator implements OAuth2TokenValidator<Jwt> {
    private final KeycloakTenantService keycloakTenantService;
    private final Map<String, JwtIssuerValidator> validators = new ConcurrentHashMap<>();
    public KeycloakJwtIssuerValidator(KeycloakTenantService keycloakTenantService) {
        this.keycloakTenantService = keycloakTenantService;
    }
    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        return validators.computeIfAbsent(toTenant(token), this::fromTenant)
                .validate(token);
    }
    protected String toTenant(Jwt jwt) {
        String issuerUrl = jwt.getIssuer().toString();
        return keycloakTenantService.extractTenantIdFromIssuerUrl(issuerUrl);
    }
    protected JwtIssuerValidator fromTenant(String issuer) {
        return keycloakTenantService.getByIssuer(issuer)
                .map(KeycloakTenant::getIssuer)
                .map(JwtIssuerValidator::new)
                .orElseThrow(() -> new IllegalArgumentException("Unknown tenant"));
    }
}
