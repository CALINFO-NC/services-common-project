package com.calinfo.api.common.matching;

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

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

@Slf4j
@ConditionalOnProperty(value = "common.configuration.matching.filter.enabled", matchIfMissing = true)
@Component
@Order(MatchingUrlFilter.ORDER_FILTER)
public class MatchingUrlFilter extends OncePerRequestFilter {

    public static final int ORDER_FILTER = 1000;


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
