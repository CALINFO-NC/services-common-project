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
import com.calinfo.api.common.security.keycloak.RealmContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class KeycloakGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {

    private final UserDetailsService userDetailsService;
    private final KeycloakProperties keycloakProperties;

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {

        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

        boolean oidcAuth = false;
        for (GrantedAuthority authority : authorities) {

            if (OidcUserAuthority.class.isInstance(authority)) {

                if (!oidcAuth) {
                    mappedAuthorities.addAll(getAuthoritiesFromOidcAuthority((OidcUserAuthority) authority));
                }
                oidcAuth = true;

            } else {
                mappedAuthorities.add(authority);
            }
        }

        return mappedAuthorities;

    }

    private Collection<? extends GrantedAuthority> getAuthoritiesFromOidcAuthority(OidcUserAuthority oidcUserAuthority){

        String login = oidcUserAuthority.getUserInfo().getClaimAsString(keycloakProperties.getPrincipalClaimName());

        String iss = oidcUserAuthority.getIdToken().getClaimAsString("iss");
        String realm = KeycloakUtils.getTenantIdFromIssuerUrl(keycloakProperties.getBaseUrl(), iss);

        String oldRealm = RealmContext.getRealm();
        try {
            RealmContext.setRealm(realm);
            return userDetailsService.loadUserByUsername(login).getAuthorities();
        } finally {
            RealmContext.setRealm(oldRealm);
        }
    }
}
