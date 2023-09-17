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

import com.calinfo.api.common.security.actuator.ActuatorProperties;
import com.calinfo.api.common.security.keycloak.KeycloakAuthorizeHttpRequestsCustomizerConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class ActuatorConfig {

    private final ActuatorProperties actuatorProperties;

    public ActuatorConfig(@Autowired(required = false) ActuatorProperties actuatorProperties) {
        this.actuatorProperties = actuatorProperties;
    }

    @Order(KeycloakAuthorizeHttpRequestsCustomizerConfig.SECURITY_FILTER_CHAIN_ORDER - 10)
    @Bean
    public SecurityFilterChain filterChainAppBasic(HttpSecurity http) throws Exception {
        http.securityMatcher(getActuatorRequestMatcher())
                .authorizeHttpRequests(auth ->
                    auth
                            .anyRequest()
                            .authenticated()
                );
        http.httpBasic(Customizer.withDefaults());
        http.authenticationProvider(new ActuatorAuthenticationProvider(this.actuatorProperties));
        return http.build();
    }


    private RequestMatcher getActuatorRequestMatcher(){
        return new AntPathRequestMatcher("/actuator/**");
    }


}
