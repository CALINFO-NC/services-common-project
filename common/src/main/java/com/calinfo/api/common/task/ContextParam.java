package com.calinfo.api.common.task;

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

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
public class ContextParam {

    public static Authentication authenticationFromUserRoles(String username, String... roles){

        List<SimpleGrantedAuthority> grants = new ArrayList<>();
        if (roles != null) {
            grants = Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        return new UsernamePasswordAuthenticationToken(username, "", grants);
    }

    public static Authentication currentAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String domain;

    private String realm;

    private Authentication authentication;
}
