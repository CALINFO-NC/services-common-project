package com.calinfo.api.common.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dalexis on 29/05/2018.
 */
@Component
@ConditionalOnProperty("common.configuration.tenant.enable")
public class TenantTestFilter extends OncePerRequestFilter {

    @Autowired
    private RequestDomainName tenantName;

    public TenantTestFilter(){
        int t = 0;
        t++;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String tenant = httpServletRequest.getHeader("tenant");
        tenantName.setValue(tenant);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
