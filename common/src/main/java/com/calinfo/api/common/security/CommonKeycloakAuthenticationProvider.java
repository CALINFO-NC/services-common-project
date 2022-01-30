package com.calinfo.api.common.security;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2022 CALINFO
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

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class CommonKeycloakAuthenticationProvider extends KeycloakAuthenticationProvider {

    private final String keycloakResource;

    private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Override
    public void setGrantedAuthoritiesMapper(GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
        super.setGrantedAuthoritiesMapper(grantedAuthoritiesMapper);
        this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
        List<GrantedAuthority> grantedAuthorities = new ArrayList();

        Iterator<String> roles = token.getAccount().getRoles().iterator();
        addRoles(grantedAuthorities, roles);

        roles = getRoles(token, getAccountResourceName());
        addRoles(grantedAuthorities, roles);

        roles = getRoles(token, keycloakResource);
        addRoles(grantedAuthorities, roles);

        return new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(), this.getAuthorities(grantedAuthorities));
    }

    private Iterator<String> getRoles(KeycloakAuthenticationToken token, String name) {

        if (token != null &&
                token.getAccount() != null &&
                token.getAccount().getKeycloakSecurityContext() != null &&
                token.getAccount().getKeycloakSecurityContext().getToken() != null &&
                token.getAccount().getKeycloakSecurityContext().getToken().getResourceAccess(name) != null &&
                token.getAccount().getKeycloakSecurityContext().getToken().getResourceAccess(name).getRoles() != null) {
            return token.getAccount().getKeycloakSecurityContext().getToken().getResourceAccess(name).getRoles().iterator();
        }

        return new ArrayList<String>().iterator();
    }

    private void addRoles(List<GrantedAuthority> grantedAuthorities, Iterator<String> roles) {
        while (roles.hasNext()) {
            String role = roles.next();
            grantedAuthorities.add(new KeycloakRole(role));
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return this.grantedAuthoritiesMapper != null ? this.grantedAuthoritiesMapper.mapAuthorities(authorities) : authorities;
    }

    protected String getAccountResourceName(){
        return "account";
    }
}
