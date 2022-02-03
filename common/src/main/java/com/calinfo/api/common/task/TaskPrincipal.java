package com.calinfo.api.common.task;

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

import lombok.Getter;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Représentation d'un pricipal
 */
public class TaskPrincipal implements Principal {

    @Getter
    private KeycloakPrincipal<KeycloakSecurityContext> keycloakPrincipal;

    @Getter
    private String name;

    @Getter
    Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

    @Getter
    private String domain;

    public TaskPrincipal(String username, String domain, Collection<? extends GrantedAuthority> authorities) {
        this.name = username;
        this.domain = domain;
        this.authorities = authorities;
    }
}
