package com.calinfo.api.common.matching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtre de sécurité sur les URLs
 */

@ConditionalOnProperty(value = "common.configuration.matching.filter.enabled", matchIfMissing = true)
@Component
@Order(MatchingUrlFilter.ORDER_FILTER)
public class MatchingUrlFilter extends OncePerRequestFilter {

    public static final int ORDER_FILTER = 1000;

    private static final Logger log = LoggerFactory.getLogger(MatchingUrlFilter.class);


    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        if (httpServletRequest.getRequestURI().contains("//")){
            log.info("MatchingFilter activate and not accept // in URI. To desactivate this filter set common.configuration.matching.filter.enabled=false in properties file");
            httpServletResponse.sendError(HttpStatus.NOT_FOUND.value());
        }
        else{
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }


}
