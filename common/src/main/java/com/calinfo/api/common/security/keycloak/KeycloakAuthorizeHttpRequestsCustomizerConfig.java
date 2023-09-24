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

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;

public interface KeycloakAuthorizeHttpRequestsCustomizerConfig {

    int SECURITY_FILTER_CHAIN_ORDER = 100;
    String ANONYMOUS_USER_NAME = "anonymousUser";

    void configAuthHttpRequest(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry request);

    default void configCsrf(CsrfConfigurer<HttpSecurity> csrf){
        csrf.disable();
    }

    default void completeConfigHttpSecurity(HttpSecurity http){
    }

    default void configSessionManagement(SessionManagementConfigurer<HttpSecurity> session){
        session.sessionAuthenticationStrategy(new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl()));
    }
}
