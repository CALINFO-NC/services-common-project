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

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class KeycloakClaimsGrantedAuthoritiesConverter implements Converter<Map<String, Object>, Collection<GrantedAuthority>> {

    private final KeycloakProperties keycloakProperties;

    @Override
    public Collection<GrantedAuthority> convert(Map<String, Object> claims) {

        if (claims == null){
            return new ArrayList<>();
        }

        List<GrantedAuthority> result = new ArrayList<>();
        result.addAll(getRoleFromClient(claims));
        result.addAll(getRoleFromRealm(claims));

        return result;
    }

    private Collection<GrantedAuthority> getRoleFromRealm(Map<String, Object> claims){

        Map<String, Object> mapRoles = (Map<String, Object>)claims.get("realm_access");

        if (mapRoles == null){
            return new ArrayList<>();
        }

        List<String> lstRole = (List<String>) mapRoles.get("roles");

        if (lstRole == null){
            return new ArrayList<>();
        }

        return lstRole.stream().map(r -> new SimpleGrantedAuthority(convertRole(r))).collect(Collectors.toList());
    }

    private Collection<GrantedAuthority> getRoleFromClient(Map<String, Object> claims){

        Map<String, Object> mapRoles = (Map<String, Object>)claims.get("resource_access");

        if (mapRoles == null){
            return new ArrayList<>();
        }

        Map<String, Object> mapRolesClient = (Map<String, Object>) mapRoles.get(keycloakProperties.getClientId());

        if (mapRolesClient == null){
            return new ArrayList<>();
        }

        List<String> lstRole = (List<String>) mapRolesClient.get("roles");

        if (lstRole == null){
            return new ArrayList<>();
        }

        return lstRole.stream().map(r -> new SimpleGrantedAuthority(convertRole(r))).collect(Collectors.toList());
    }


    protected String convertRole(String role){

        if (role == null || role.startsWith("ROLE_")){
            return role;
        }

        return String.format("ROLE_%s", role);
    }
}
