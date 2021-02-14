package com.calinfo.api.common.security;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
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


import com.calinfo.api.common.matching.MatchingUrlFilter;
import com.calinfo.api.common.tenant.DomainContext;
import lombok.RequiredArgsConstructor;
import org.keycloak.KeycloakPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;

@RequiredArgsConstructor
/*@ConditionalOnProperty("common.configuration.security.enabled")
@Component
@Order(CommonSecurityUrlFilter.ORDER_FILTER)*/
public class CommonSecurityUrlFilter extends OncePerRequestFilter {

    public static final int ORDER_FILTER = MatchingUrlFilter.ORDER_FILTER + 10;

    public static final String HEADER_DOMAIN = "X-Domain";

    private final SecurityProperties securityProperties;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {

        String oldDomain = DomainContext.getDomain();
        try {
            DomainContext.setDomain(httpServletRequest.getHeader(HEADER_DOMAIN));
            Principal principal = httpServletRequest.getUserPrincipal();
            CommonPrincipal commonPrincipal;

            if (principal != null && principal instanceof KeycloakPrincipal) {
                KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) httpServletRequest.getUserPrincipal();
                commonPrincipal = new CommonPrincipal(keycloakPrincipal, DomainContext.getDomain());
            }
            else{
                commonPrincipal = new CommonPrincipal(securityProperties.getAnonymousLogin(), DomainContext.getDomain(), new ArrayList<>());
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(commonPrincipal, "", commonPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
        finally {
            DomainContext.setDomain(oldDomain);
        }
    }

}
