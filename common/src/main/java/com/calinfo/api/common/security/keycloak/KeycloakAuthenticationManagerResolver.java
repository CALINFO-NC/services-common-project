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

import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeycloakAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {
    private final KeycloakTenantService keycloakTenantService;
    private final JwtDecoder jwtDecoder;
    private final BearerTokenResolver resolver = new DefaultBearerTokenResolver();
    private final UserDetailsService userDetailsService;
    private final KeycloakProperties keycloakProperties;
    private final Map<String, AuthenticationManager> authenticationManagers = new ConcurrentHashMap<>();
    public KeycloakAuthenticationManagerResolver(KeycloakTenantService keycloakTenantService,
                                                 KeycloakProperties keycloakProperties,
                                                 UserDetailsService userDetailsService,
                                                 JwtDecoder jwtDecoder) {

        this.keycloakTenantService = keycloakTenantService;
        this.jwtDecoder = jwtDecoder;
        this.userDetailsService = userDetailsService;
        this.keycloakProperties = keycloakProperties;
    }
    @Override
    public AuthenticationManager resolve(HttpServletRequest request) {
        return authenticationManagers.computeIfAbsent(toTenant(request), this::fromTenant);
    }
    protected String toTenant(HttpServletRequest request) {
        try {
            String token = resolver.resolve(request);
            String issuerUrl = JWTParser.parse(token).getJWTClaimsSet().getIssuer();
            return keycloakTenantService.extractTenantIdFromIssuerUrl(issuerUrl);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    protected AuthenticationManager fromTenant(String tenant) {

        return keycloakTenantService.getByIssuer(tenant)
                .map(KeycloakTenant::getIssuer)
                .map(i -> new KeycloakJwtAuthenticationManager(keycloakProperties, userDetailsService, jwtDecoder))
                .orElseThrow(() -> new InvalidBearerTokenException("Unknown tenant: " + tenant));

    }
}
