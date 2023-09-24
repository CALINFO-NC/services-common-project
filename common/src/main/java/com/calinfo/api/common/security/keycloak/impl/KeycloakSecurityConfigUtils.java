package com.calinfo.api.common.security.keycloak.impl;

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

import com.calinfo.api.common.security.keycloak.KeycloakProperties;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import com.nimbusds.jwt.proc.JWTProcessor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

class KeycloakSecurityConfigUtils {

    public static JWTClaimsSetAwareJWSKeySelector<SecurityContext> getJwtSelector(KeycloakTenantService keycloakTenantService){
        return new KeycloakJWSKeySelector(keycloakTenantService);
    }

    public static OAuth2TokenValidator<Jwt> getOAuth2TokenValidator(KeycloakTenantService keycloakTenantService){
        return new KeycloakJwtIssuerValidator(keycloakTenantService);
    }

    public static JWTProcessor<SecurityContext> getJwtProcessor(JWTClaimsSetAwareJWSKeySelector<SecurityContext> jwtSeletor){
        return new KeycloakJWTProcessor(jwtSeletor);
    }

    public static JWTProcessor<SecurityContext> getJwtProcessor(KeycloakTenantService keycloakTenantService){

        JWTClaimsSetAwareJWSKeySelector<SecurityContext> jwtSeletor = getJwtSelector(keycloakTenantService);

        return getJwtProcessor(jwtSeletor);
    }

    public static JwtDecoder getJwtDecoder(JWTProcessor jwtProcessor, OAuth2TokenValidator<Jwt> oAuth2TokenValidator){
        NimbusJwtDecoder decoder = new NimbusJwtDecoder(jwtProcessor);
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>
                (JwtValidators.createDefault(), oAuth2TokenValidator);
        decoder.setJwtValidator(validator);
        return decoder;
    }

    public static JwtDecoder getJwtDecoder(KeycloakTenantService keycloakTenantService){

        JWTClaimsSetAwareJWSKeySelector<SecurityContext> jwtSeletor = getJwtSelector(keycloakTenantService);
        JWTProcessor<SecurityContext> jwtProcessor = getJwtProcessor(jwtSeletor);
        OAuth2TokenValidator<Jwt> oAuth2TokenValidator = getOAuth2TokenValidator(keycloakTenantService);

        return getJwtDecoder(jwtProcessor, oAuth2TokenValidator);
    }

    public static AuthenticationManagerResolver<HttpServletRequest> getAuthenticationManagerResolver(KeycloakTenantService keycloakTenantService,
                                                                                                     KeycloakProperties keycloakProperties,
                                                                                                     UserDetailsService userDetailsService,
                                                                                                     JwtDecoder jwtDecoder) {
        return new KeycloakAuthenticationManagerResolver(keycloakTenantService, keycloakProperties, userDetailsService, jwtDecoder);
    }

    public static AuthenticationManagerResolver<HttpServletRequest> getAuthenticationManagerResolver(KeycloakTenantService keycloakTenantService,
                                                                                                     KeycloakProperties keycloakProperties,
                                                                                                     UserDetailsService userDetailsService){

        JwtDecoder jwtDecoder = getJwtDecoder(keycloakTenantService);
        return getAuthenticationManagerResolver(keycloakTenantService, keycloakProperties, userDetailsService, jwtDecoder);
    }

    public static ClientRegistrationRepository getClientRegistrationRepository(KeycloakProperties keycloakProperties){
        return new KeycloakClientRegistrationRepository(keycloakProperties);
    }


    public static GrantedAuthoritiesMapper getGrantedAuthoritiesMapper(KeycloakProperties keycloakProperties,
                                                                       UserDetailsService userDetailsService){

        return new KeycloakGrantedAuthoritiesMapper(userDetailsService, keycloakProperties);
    }


}
