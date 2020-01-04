package com.calinfo.api.common.security;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2020 CALINFO
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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


/**
 * Implémentation du principal par le common
 */
@EqualsAndHashCode
public class CommonPrincipal extends AbstractCommonPrincipal {

    @Getter
    private String domain;

    @Getter
    private String apiKey;

    @Getter
    private String initialToken;

    public CommonPrincipal(String apiKey, String initialToken, String domain, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.domain = domain;
        this.apiKey = apiKey;
        this.initialToken = initialToken;
    }
}
