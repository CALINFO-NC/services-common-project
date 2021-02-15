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

import lombok.Getter;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Représentation d'un pricipal
 */
public class CommonPrincipal extends User {

    @Getter
    private KeycloakPrincipal<KeycloakSecurityContext> keycloakPrincipal;

    @Getter
    private String domain;

    public CommonPrincipal(String username, String domain, Collection<? extends GrantedAuthority> authorities) {
        super(username, "", authorities);
        this.domain = domain;
    }

    public CommonPrincipal(KeycloakPrincipal<KeycloakSecurityContext> keycloakPrincipal, String domain) {
        super(keycloakPrincipal.getName(), "", keycloakPrincipal.getKeycloakSecurityContext().getToken().getRealmAccess().getRoles().stream().map(item -> new SimpleGrantedAuthority("ROLE_" + item)).collect(Collectors.toList()));
        this.keycloakPrincipal = keycloakPrincipal;
        this.domain = domain;
    }

}
