package com.calinfo.api.common.security;

import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.ex.handler.ResponseEntityExceptionHandler;
import com.calinfo.api.common.matching.MatchingUrlFilter;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@ConditionalOnProperty("common.configuration.security.enabled")
@Component
@Order(CommonSecurityUrlFilter.ORDER_FILTER)
public class CommonSecurityUrlFilter extends OncePerRequestFilter {

    public static final int ORDER_FILTER = MatchingUrlFilter.ORDER_FILTER + 10;

    private static final Logger log = LoggerFactory.getLogger(CommonSecurityUrlFilter.class);

    /**
     * Nom du header contenant le token d'authentification
     */
    public static final String HEADER_AUTHORIZATION_NAME = "Authorization";

    /**
     * Nom du header contenant le token d'authentification
     */
    public static final String HEADER_API_KEY = "X-ApiKey";

    /**
     * Préfixe du token de sécurité
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * Propriété de sécurité
     */
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * Classe chargé du traitement des exceptions
     */
    @Autowired
    private ResponseEntityExceptionHandler responseEntityExceptionHandler;

    /**
     * Classe permettant de gérer le principal dse trouvant dans le context
     */
    @Autowired
    private PrincipalFactory principalFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        try{
            initPrincipal(httpServletRequest, httpServletResponse);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
        catch(ExpiredJwtException e){
            catchMessageStatusException(new MessageStatusException(HttpStatus.FORBIDDEN, e.getMessage()), httpServletResponse);
        }
        catch(MessageStatusException e){
            catchMessageStatusException(e, httpServletResponse);
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
            ResponseEntity<String> response = responseEntityExceptionHandler.messageThrowable(e);
            httpServletResponse.sendError(response.getStatusCodeValue(), response.getBody());
        }
    }

    private void catchMessageStatusException(MessageStatusException e, HttpServletResponse httpServletResponse) throws IOException {

        if (e.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error(e.getMessage(), e);
        }
        else{
            log.info(e.getMessage());
            log.debug(e.getMessage(), e);
        }
        ResponseEntity<String> response = responseEntityExceptionHandler.messageStatusException(e);
        httpServletResponse.sendError(response.getStatusCodeValue(), response.getBody());
    }

    private void initPrincipal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        // Vérifier que l'URL est privée
        boolean isPrivate = isPrivateUrl(httpServletRequest.getRequestURI());

        // Récupérer le token d'identification
        String token = httpServletRequest.getHeader(HEADER_AUTHORIZATION_NAME);
        String apiKey = httpServletRequest.getHeader(HEADER_API_KEY);

        if (isPrivate) {

            if (StringUtils.isBlank(token)){
                throw new MessageStatusException(HttpStatus.FORBIDDEN, "Token required");
            }

            if (!token.startsWith(BEARER_PREFIX)){
                throw new MessageStatusException(HttpStatus.FORBIDDEN, String.format("JWT '%s' prefix not found", BEARER_PREFIX));
            }

            token = token.substring(BEARER_PREFIX.length());
            token = principalFactory.setPrincipalInContextFromToken(token, apiKey);
            token = String.format("%s%s", BEARER_PREFIX, token);

            httpServletResponse.setHeader(HEADER_AUTHORIZATION_NAME, token);
        }
        else{
            principalFactory.setAnonymousPrincipalInContext();
        }
    }



    /**
     * Permet de définir si une URL est privée
     * @param reqUri URL
     * @return true si l'URL est privée
     */
    protected boolean isPrivateUrl(String reqUri){

        boolean isPrivate = false;
        if (reqUri.matches(securityProperties.getPrivateUrlRegex())) {
            isPrivate = true;
        }
        return isPrivate;
    }

}
