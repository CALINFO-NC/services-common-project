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

import lombok.Data;

@Data
public class KeycloakUrlProperties {

    public static final String DEFAULT_LOGIN = "/security/login";
    public static final String DEFAULT_LOGOUT = "/security/logout";
    public static final String DEFAULT_USER_JSON_DETAILS = "/security/user-details";
    public static final String DEFAULT_KEYCLOAK_ACCOUNT = "/security/account";
    public static final String DEFAULT_KEYCLOAK_CONSOLE = "/security/console";

    private String login = DEFAULT_LOGIN;
    private String logout = DEFAULT_LOGOUT;
    private String userJsonDetails = DEFAULT_USER_JSON_DETAILS;
    private String keycloakAccount = DEFAULT_KEYCLOAK_ACCOUNT;
    private String keycloakConsole = DEFAULT_KEYCLOAK_CONSOLE;
}
