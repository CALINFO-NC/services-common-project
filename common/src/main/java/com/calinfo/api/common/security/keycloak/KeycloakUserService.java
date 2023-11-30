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

import com.calinfo.api.common.config.ApplicationProperties;
import com.calinfo.api.common.security.DefaultUserDetailsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientMappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@ConditionalOnBean(KeycloakAuthorizeHttpRequestsCustomizerConfig.class)
@Slf4j
@RequiredArgsConstructor
@Service
public class KeycloakUserService implements UserDetailsService {

    private final KeycloakManager keycloakManager;
    private final ApplicationProperties applicationProperties;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        try(Keycloak keycloak = keycloakManager.getRootHandle()) {

            DefaultUserDetailsDto octopusUserDetailDto = new DefaultUserDetailsDto();
            octopusUserDetailDto.setUsername(login);

            List<String> roles = new ArrayList<>();
            if (findUserResource(keycloak, login) != null) {

                // TODO ajouter les autres champs de UserDetails

                try {
                    roles.addAll(getRoleFromKeycloackClient(keycloak, login, applicationProperties.getId()));
                    roles.addAll(getRoleFromKeycloackRealm(keycloak, login));

                } catch (Exception e) {
                    log.debug(e.getMessage(), e);
                    log.info(e.getMessage());
                }

            }

            octopusUserDetailDto.setAuthorities(roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));


            return octopusUserDetailDto;
        }
    }


    private List<String> getRoleFromKeycloackRealm(Keycloak keycloak, String login){

        List<String> roles = new ArrayList<>();
        UserResource userResource = findUserResource(keycloak, login);

        if (userResource == null){
            return roles;
        }

        List<RoleRepresentation> lstRoleRep = userResource.roles().getAll().getRealmMappings();
        if (lstRoleRep != null) {
            roles.addAll(toSpringRole(lstRoleRep.stream().map(RoleRepresentation::getName).toList()));
        }

        return roles;
    }

    private List<String> getRoleFromKeycloackClient(Keycloak keycloak, String login, String clientId){

        List<String> roles = new ArrayList<>();
        UserResource userResource = findUserResource(keycloak, login);

        if (userResource == null){
            return roles;
        }

        Map<String, ClientMappingsRepresentation> mapClientMapping = userResource.roles().getAll().getClientMappings();
        if (mapClientMapping != null){
            ClientMappingsRepresentation clientMappingsRepresentation = mapClientMapping.get(clientId);

            if (clientMappingsRepresentation != null) {
                List<RoleRepresentation> clientRole = clientMappingsRepresentation.getMappings();

                if (clientRole != null) {
                    roles.addAll(toSpringRole(clientRole.stream().map(RoleRepresentation::getName).toList()));
                }
            }
        }

        return roles;
    }

    private UserResource findUserResource(Keycloak keycloak, String login){
        UsersResource usersResource = keycloak.realm(RealmContext.getRealm()).users();
        Optional<UserRepresentation> userRepresentation = usersResource.search(login).stream().filter(r -> Objects.equals(r.getUsername(), login)).findFirst();

        return userRepresentation.map(representation -> usersResource.get(representation.getId())).orElse(null);

    }

    private List<String> toSpringRole(List<String> lstRole){
        return lstRole.stream().map(this::toSpringRole).toList();
    }

    protected String toSpringRole(String originalRole){
        return String.format("ROLE_%s", originalRole);
    }
}
