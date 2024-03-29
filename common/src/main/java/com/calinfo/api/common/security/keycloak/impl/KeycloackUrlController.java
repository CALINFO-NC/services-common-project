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

import com.calinfo.api.common.security.UserDetailsAuthentication;
import com.calinfo.api.common.security.keycloak.*;
import com.calinfo.api.common.utils.MiscUtils;
import com.calinfo.api.common.utils.SecurityUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ConditionalOnProperty(prefix = "common.configuration.security.keycloak.urls", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(KeycloakAuthorizeHttpRequestsCustomizerConfig.class)
@Slf4j
@RequiredArgsConstructor
@RequestMapping
@Controller
class KeycloackUrlController {

    public static final String LOCATION = "Location";
    private final KeycloakManager keycloakManager;
    private final KeycloakProperties keycloakProperties;
    private final UserDetailsService userDetailsService;

    @SneakyThrows
    @GetMapping(value = "${common.configuration.security.keycloak.urls.login:" + KeycloakUrlProperties.DEFAULT_LOGIN + "}")
    public String login() {

        if (SecurityUtils.isUserConnected()) {
            return "redirect:/";
        }
        else{
            return String.format("redirect:/oauth2/authorization/%s", RealmContext.getRealm());
        }
    }

    @GetMapping(value = "${common.configuration.security.keycloak.urls.logout:" + KeycloakUrlProperties.DEFAULT_LOGOUT + "}")
    public String logout(HttpServletRequest request) throws ServletException {

        try(Keycloak keycloak = this.keycloakManager.getRootHandle()) {

            RealmResource realmResource = keycloak.realm(RealmContext.getRealm());
            String login = SecurityUtils.getUsernameFromSecurityContext();

            List<UserRepresentation> lstUserRepresentation = realmResource.users().search(login).stream().filter(r -> Objects.equals(r.getUsername(), login)).toList();

            if (lstUserRepresentation.size() > 1) {
                log.warn(String.format("Attention, plusieurs utilisateurs identifiés pour le login '%s' lors de la déconnexion", login));
            }

            if (lstUserRepresentation.size() < 1 && !Objects.equals(login, KeycloakAuthorizeHttpRequestsCustomizerConfig.ANONYMOUS_USER_NAME)) {
                log.warn(String.format("Attention, aucun utilisateur identifié pour le login '%s' lors de la déconnexion", login));
            }

            lstUserRepresentation.forEach(userRepresentation -> {
                UserResource userResource = realmResource.users().get(userRepresentation.getId());
                userResource.logout();
            });

            request.logout();
            return "redirect:/";
        }
    }

    @ResponseBody
    @GetMapping(value = "${common.configuration.security.keycloak.urls.user-json-details:" + KeycloakUrlProperties.DEFAULT_USER_JSON_DETAILS + "}")
    public UserDetails userJsonDetail(){
        if (SecurityContextHolder.getContext().getAuthentication() instanceof UserDetailsAuthentication auth){
            return (UserDetails) auth.getDetails();
        }
        String login = SecurityUtils.getUsernameFromSecurityContext();
        return userDetailsService.loadUserByUsername(login);
    }

    @GetMapping(value = {"${common.configuration.security.keycloak.urls.keycloak-administration:" + KeycloakUrlProperties.DEFAULT_KEYCLOAK_CONSOLE + "}",
            "${common.configuration.security.keycloak.urls.keycloak-administration:" + KeycloakUrlProperties.DEFAULT_KEYCLOAK_CONSOLE + "}/{page}",
            "${common.configuration.security.keycloak.urls.keycloak-administration:" + KeycloakUrlProperties.DEFAULT_KEYCLOAK_CONSOLE + "}/{page}/{idPage}"})
    public void keycloakAdministration(HttpServletResponse httpServletResponse,
                                       @PathVariable(name = "page", required = false) String page,
                                       @PathVariable(name = "idPage", required = false) String idPage) {

        if (StringUtils.isBlank(page)) {
            httpServletResponse.setHeader(LOCATION, String.format("%s/admin/%s/console/", getAuthServerUrl(), RealmContext.getRealm()));
        }
        else{

            if (StringUtils.isBlank(idPage)) {
                httpServletResponse.setHeader(LOCATION, String.format("%s/admin/%s/console/#/%s/%s",
                        getAuthServerUrl(),
                        RealmContext.getRealm(),
                        RealmContext.getRealm(),
                        page));
            }
            else {
                httpServletResponse.setHeader(LOCATION, String.format("%s/admin/%s/console/#/%s/%s/%s",
                        getAuthServerUrl(),
                        RealmContext.getRealm(),
                        RealmContext.getRealm(),
                        page,
                        idPage));
            }
        }
        httpServletResponse.setStatus(302);
    }

    @GetMapping(value = {"${common.configuration.security.keycloak.urls.keycloak-user-account:" + KeycloakUrlProperties.DEFAULT_KEYCLOAK_ACCOUNT + "}",
            "${common.configuration.security.keycloak.urls.keycloak-user-account:" + KeycloakUrlProperties.DEFAULT_KEYCLOAK_ACCOUNT + "}/{page}"})
    public void keycloakUserAccount(HttpServletResponse httpServletResponse, @PathVariable(name = "page", required = false) String page) {

        if (StringUtils.isBlank(page)) {
            httpServletResponse.setHeader(LOCATION, String.format("%s/realms/%s/account/", getAuthServerUrl(), RealmContext.getRealm()));
        }
        else{
            httpServletResponse.setHeader(LOCATION, String.format("%s/realms/%s/account/#/%s",
                    getAuthServerUrl(),
                    RealmContext.getRealm(),
                    page));
        }

        httpServletResponse.setStatus(302);
    }

    private String getAuthServerUrl(){
        return MiscUtils.formatEndUrl(keycloakProperties.getBaseUrl());
    }
}
