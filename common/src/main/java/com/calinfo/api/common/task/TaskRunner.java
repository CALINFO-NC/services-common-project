package com.calinfo.api.common.task;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 CALINFO
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

import com.calinfo.api.common.security.AbstractCommonPrincipal;
import com.calinfo.api.common.security.CommonPrincipal;
import com.calinfo.api.common.security.SecurityProperties;
import com.calinfo.api.common.tenant.DomainContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class TaskRunner {

    @Autowired
    private SecurityProperties securityProperties;

    public <T> Optional<T> run (String username, String domainName, String[] roles, Task<T> task) throws TaskException {

        String actualDomain = DomainContext.getDomain();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        try {
            DomainContext.setDomain(domainName);

            List<SimpleGrantedAuthority> grants = Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            // Mettre en place l'authentification
            AbstractCommonPrincipal principal = new CommonPrincipal(null, null, domainName, username, "", grants);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Exécuter la tâche
            return task.run();

        }
        finally {
            DomainContext.setDomain(actualDomain);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }

    public <T> Optional<T> run (String domainName, String[] roles, Task<T> task) throws TaskException {
        return run(securityProperties.getSystemLogin(), domainName, roles, task);
    }

    public <T> Optional<T> run (String domainName, Task<T> task) throws TaskException {
        return run(securityProperties.getSystemLogin(), domainName, new String[]{}, task);
    }

    public <T> Optional<T> run (Task<T> task) throws TaskException {
        return run(securityProperties.getSystemLogin(), null, new String[]{}, task);
    }
}
