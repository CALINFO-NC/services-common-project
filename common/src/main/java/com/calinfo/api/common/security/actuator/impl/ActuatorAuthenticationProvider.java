package com.calinfo.api.common.security.actuator.impl;

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

import com.calinfo.api.common.security.DefaultUserDetailsDto;
import com.calinfo.api.common.security.UserDetailsAuthentication;
import com.calinfo.api.common.security.actuator.ActuatorProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.UUID;

public class ActuatorAuthenticationProvider implements AuthenticationProvider {

    private final ActuatorProperties actuatorProperties;

    public ActuatorAuthenticationProvider(@Autowired(required = false) ActuatorProperties actuatorProperties){
        this.actuatorProperties = actuatorProperties;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials()
                .toString();

        String internalUsername = String.format("%s-%s-%s", UUID.randomUUID(), Math.random(), System.currentTimeMillis());
        String internalPassword = String.format("%s-%s-%s", UUID.randomUUID(), Math.random(), System.currentTimeMillis());

        if (actuatorProperties != null &&
                !StringUtils.isBlank(actuatorProperties.getLogin()) &&
                !StringUtils.isBlank(actuatorProperties.getPassword())){

            internalUsername = this.actuatorProperties.getLogin();
            internalPassword = this.actuatorProperties.getPassword();
        }

        DefaultUserDetailsDto userDetails = new DefaultUserDetailsDto();
        userDetails.setUsername(username);
        userDetails.setAccountNonExpired(true);
        userDetails.setAccountNonLocked(true);
        userDetails.setCredentialsNonExpired(true);

        if (internalUsername.equals(username) && internalPassword.equals(password)) {
            return new UserDetailsAuthentication(userDetails, null);
        } else {
            throw new BadCredentialsException("External system authentication failed");
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
