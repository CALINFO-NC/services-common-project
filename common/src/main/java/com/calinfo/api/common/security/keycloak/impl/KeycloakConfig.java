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
import com.calinfo.api.common.security.keycloak.KeycloakAuthorizeHttpRequestsCustomizerConfig;
import com.calinfo.api.common.security.keycloak.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.stereotype.Component;

@ConditionalOnBean(KeycloakAuthorizeHttpRequestsCustomizerConfig.class)
@Component
@RequiredArgsConstructor
class KeycloakConfig {


    private final KeycloakTenantService keycloakTenantService;
    private final KeycloakProperties keycloakProperties;
    private final UserDetailsService userDetailsService;
    private final KeycloakAuthorizeHttpRequestsCustomizerConfig keycloakAuthorizeHttpRequestsCustomizerConfig;

    @Order(KeycloakAuthorizeHttpRequestsCustomizerConfig.SECURITY_FILTER_CHAIN_ORDER)
    @Bean
    public SecurityFilterChain filterChainAppOauth(HttpSecurity http) throws Exception {

        http.csrf(crsf -> {
           crsf.disable();
        });
        http.authorizeHttpRequests(keycloakAuthorizeHttpRequestsCustomizerConfig::config);

        http.userDetailsService(userDetailsService);
        http.oauth2ResourceServer(oauth2 -> oauth2.authenticationManagerResolver(KeycloakSecurityConfigUtils.getAuthenticationManagerResolver(keycloakTenantService, keycloakProperties, userDetailsService)));
        http.sessionManagement(session -> session.sessionAuthenticationStrategy(new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl())));
        http.oauth2Login(login -> {
            login.clientRegistrationRepository(KeycloakSecurityConfigUtils.getClientRegistrationRepository(this.keycloakProperties));
            login.loginPage(keycloakProperties.getUrls().getLogin());
        });

        return http.build();
    }
}
