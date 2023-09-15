package com.calinfo.api.common.tenant;

import com.calinfo.api.common.domain.DomainContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Created by dalexis on 29/05/2018.
 */
public class TenantTestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String tenant = httpServletRequest.getHeader("tenant");
        DomainContext.setDomain(tenant);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
