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

import com.calinfo.api.common.manager.ApiKeyManager;
import com.calinfo.api.common.utils.SecurityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by dalexis on 05/04/2018.
 */

@Component
public class PrincipalFactory {


    private static final Logger log = LoggerFactory.getLogger(PrincipalFactory.class);

    /**
     * Propriété de sécurité
     */
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * Lorsq'un token n'est plus valide, on peut demander un rafraichissement de celui-ci
     */
    @Autowired(required = false)
    private ApiKeyManager apiKeyManager;

    /**
     * Cette méthode permet d'insérer dans le context courant le principal connecté à partir des tokens
     *
     * @param token Token à décrypter
     * @param apiKey Clé d'api permettant de renouveller le token
     * @return Nouveau token généré dans le cas ou il a été nécessaire de rafraichir le token
     */
    public String setPrincipalInContextFromToken(String token, String apiKey){

        String tokenForPrincipal = token;

        AbstractCommonPrincipal principal;
        try {
            principal = SecurityUtils.getPrincipalFromTokens(tokenForPrincipal, apiKey, securityProperties.getPublicKeyCertificat());
        }
        catch (ExpiredJwtException e){

            log.info(e.getMessage());

            tokenForPrincipal = refreshToken(apiKey);

            if (tokenForPrincipal != null){
                principal = SecurityUtils.getPrincipalFromTokens(tokenForPrincipal, apiKey, securityProperties.getPublicKeyCertificat());
            }
            else{
                log.info("Impossible to refresh token");
                throw e;
            }
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenForPrincipal;
    }

    /**
     * Cette méthode permet d'insérer un principal "anonyumous" dans le context courant
     */
    public void setAnonymousPrincipalInContext(){
        CommonPrincipal principal = new CommonPrincipal(null, null,null, securityProperties.getAnonymousLogin(), "", new ArrayList<>());

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Permet de rafraichir le token
     * @param apiKey
     * @return
     */
    private String refreshToken(String apiKey) {

        if (apiKey == null){
            return null;
        }

        return apiKeyManager.refreshToken(apiKey);
    }
}
