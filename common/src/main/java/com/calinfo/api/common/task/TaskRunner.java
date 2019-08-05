package com.calinfo.api.common.task;

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
import java.util.stream.Collectors;


@Component
public class TaskRunner {

    @Autowired
    private SecurityProperties securityProperties;

    public void run (String username, String domainName, String[] roles, Task task) throws TaskException {

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
            task.run();

        }
        finally {
            SecurityContextHolder.getContext().setAuthentication(auth);
            DomainContext.setDomain(actualDomain);
        }
    }

    public void run (String domainName, String[] roles, Task task) throws TaskException {
        run(securityProperties.getSystemLogin(), domainName, roles, task);
    }

    public void run (String domainName, Task task) throws TaskException {
        run(securityProperties.getSystemLogin(), domainName, new String[]{}, task);
    }

    public void run (Task task) throws TaskException {
        run(securityProperties.getSystemLogin(), null, new String[]{}, task);
    }
}
