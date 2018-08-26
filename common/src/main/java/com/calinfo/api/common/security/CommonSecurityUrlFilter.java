package com.calinfo.api.common.security;

import com.calinfo.api.common.config.ApplicationProperties;
import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.ex.handler.ResponseEntityExceptionHandler;
import com.calinfo.api.common.manager.ApiKeyManager;
import com.calinfo.api.common.matching.MatchingUrlFilter;
import com.calinfo.api.common.utils.SecurityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
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
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * Lorsq'un token n'est plus valide, le filtre peux demander un rafraichissement de celui-ci
     */
    @Autowired(required = false)
    private ApiKeyManager apiKeyManager;

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
        }
        ResponseEntity<String> response = responseEntityExceptionHandler.messageStatusException(e);
        httpServletResponse.sendError(response.getStatusCodeValue(), response.getBody());
    }

    private void initPrincipal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        // Vérifier que l'URL est privée
        boolean isPrivate = isPrivateUrl(httpServletRequest.getRequestURI());

        AbstractCommonPrincipal principal = createAnonymousPrincipal();

        // Récupérer le token d'identification
        String token = httpServletRequest.getHeader(HEADER_AUTHORIZATION_NAME);

        if (isPrivate) {

            if (StringUtils.isBlank(token)){
                throw new MessageStatusException(HttpStatus.FORBIDDEN, "Token required");
            }

            try {
                principal = createAuthentifiedPrincipal(token);
            }
            catch (ExpiredJwtException e){

                log.info(e.getMessage());

                token = refreshToken(httpServletRequest.getHeader(HEADER_API_KEY));

                if (token != null){
                    token = String.format("%s%s", BEARER_PREFIX, token);
                    principal = createAuthentifiedPrincipal(token);
                }
                else{
                    log.info("Impossible to refresh token");
                    throw e;
                }
            }

            httpServletResponse.setHeader(HEADER_AUTHORIZATION_NAME, token);
        }

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

    /**
     * Permet de récupérer un pricipal e nfonction du token
     * @param token
     * @return
     */
    protected AbstractCommonPrincipal createAuthentifiedPrincipal(@NotNull String token) {

        if (!token.startsWith(BEARER_PREFIX)){
            throw new MessageStatusException(HttpStatus.FORBIDDEN, String.format("JWT '%s' prefix not found", BEARER_PREFIX));
        }

        token = token.substring(BEARER_PREFIX.length());

        JwtUser user;
        try{
            user = SecurityUtils.getUserFromJwt(token, securityProperties.getPublicKeyValue());
        }
        catch(InvalidKeySpecException | NoSuchAlgorithmException e){
            throw new MessageStatusException(HttpStatus.FORBIDDEN, "JWT Expired or invalid");
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
