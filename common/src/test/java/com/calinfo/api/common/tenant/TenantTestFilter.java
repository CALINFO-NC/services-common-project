package com.calinfo.api.common.tenant;

import com.calinfo.api.common.security.PrincipalManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dalexis on 29/05/2018.
 */
public class TenantTestFilter extends OncePerRequestFilter {

    @Autowired
    private PrincipalManager principalManager;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String tenant = httpServletRequest.getHeader("tenant");
        DomainContext.setDomain(tenant);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
