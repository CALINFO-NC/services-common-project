package com.calinfo.api.common.security;

import com.calinfo.api.common.config.ApplicationProperties;
import com.calinfo.api.common.ex.ApplicationErrorException;
import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.ex.handler.ResponseEntityExceptionHandler;
import com.calinfo.api.common.utils.SecurityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtre de sécurité sur les URLs
 */
@ConditionalOnProperty("common.configuration.security.enable")
@Component
public class CommonSecurityUrlFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(CommonSecurityUrlFilter.class);

    /**
     * Nom du header contenant le token d'authentification
     */
    public static final String HEADER_AUTHORIZATION_NAME = "Authorization";

    /**
     * Préfixe du token de sécurité
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Propriété de sécurité
     */
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * Propriété de l'application
     */
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Classe chargé du traitement des exceptions
     */
    @Autowired
    private ResponseEntityExceptionHandler responseEntityExceptionHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        try {
            // Vérifier que l'URL est privée
            boolean isPrivate = isPrivateUrl(httpServletRequest.getRequestURI());


            AbstractCommonPrincipal principal = createAnonymousPrincipal();
            if (isPrivate) {
                principal = createPrincipal(httpServletRequest);
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
        catch(MessageStatusException e){

            if (e.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
                log.error(e.getMessage(), e);
            }
            else{
                log.info(e.getMessage());
            }
            ResponseEntity<String> response = responseEntityExceptionHandler.messageStatusException(e);
            httpServletResponse.sendError(response.getStatusCodeValue(), response.getBody());
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
            ResponseEntity<String> response = responseEntityExceptionHandler.messageThrowable(e);
            httpServletResponse.sendError(response.getStatusCodeValue(), response.getBody());
        }
    }

    /**
     * Permet de récupérer un principal en fonction des données de l'URL
     * @param httpServletRequest
     */
    protected AbstractCommonPrincipal createPrincipal(HttpServletRequest httpServletRequest) {


        // Définir que l'URL est privée
        boolean isPrivate = isPrivateUrl(httpServletRequest.getRequestURI());


        // Récupérer le token d'identification
        String token = httpServletRequest.getHeader(HEADER_AUTHORIZATION_NAME);

        if (isPrivate && !StringUtils.isBlank(token)){
            return createAuthentifiedPrincipal(token);
        }
        else if (StringUtils.isBlank(token) && !isPrivate){
            return createAnonymousPrincipal();
        }
        else{
            throw new MessageStatusException(HttpStatus.FORBIDDEN, "JWT empty");
        }
    }

    /**
     * Permet de récupérer un pricipal e nfonction du token
     * @param token
     * @return
     */
    protected AbstractCommonPrincipal createAuthentifiedPrincipal(String token){

        if (!token.startsWith(BEARER_PREFIX)){
            throw new MessageStatusException(HttpStatus.FORBIDDEN, String.format("JWT '%s' prefix not found", BEARER_PREFIX));
        }

        token = token.substring(BEARER_PREFIX.length());

        JwtUser user;
        try{
            user = SecurityUtils.getUserFromJwt(token, securityProperties.getPublicKeyValue());
        }
        catch(ExpiredJwtException e){
            throw new MessageStatusException(HttpStatus.FORBIDDEN, "JWT Expired");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ApplicationErrorException(e);
        }

        List<String> lstRoles = user.getRoles();
        if (lstRoles == null){
            lstRoles = new ArrayList<>();
        }
        List<? extends GrantedAuthority> authorities = lstRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        // Création du principal
        return new CommonPrincipal(user.getDomain(), user.getLogin(), "", authorities);
    }

    /**
     * Permet d erécupérer un principal s'il n'y a pas de token
     * @return
     */
    protected AbstractCommonPrincipal createAnonymousPrincipal(){
        return new CommonPrincipal(null, securityProperties.getAnonymousLogin(), "", new ArrayList<>());
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
