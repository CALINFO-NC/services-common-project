package com.calinfo.api.common.security;

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

import com.calinfo.api.common.dto.Dto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Getter
@Setter
public class DefaultUserDetailsDto implements Dto, UserDetails {

    private Collection<? extends GrantedAuthority> authorities = Collections.EMPTY_LIST;

    private String password;

    private String username;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    private boolean enabled = true;

    public static DefaultUserDetailsDto of(UserDetails userDetails){
        DefaultUserDetailsDto result = new DefaultUserDetailsDto();

        result.password = userDetails.getPassword();
        result.username = userDetails.getUsername();
        result.accountNonExpired = userDetails.isAccountNonExpired();
        result.accountNonLocked = userDetails.isAccountNonLocked();
        result.credentialsNonExpired = userDetails.isCredentialsNonExpired();
        result.enabled = userDetails.isEnabled();

        result.authorities = userDetails.getAuthorities().stream().map(i -> new SimpleGrantedAuthority(i.getAuthority())).collect(Collectors.toList());

        return result;
    }
}
