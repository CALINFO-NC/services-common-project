package com.calinfo.api.common.mocks;

import com.calinfo.api.common.security.CommonPrincipal;
import com.calinfo.api.common.security.JwtUser;
import com.calinfo.api.common.utils.MiscUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dalexis on 06/01/2018.
 */
@Controller
public class SecurityFilterController {


    @GetMapping(value = "/api/v1/private/mock/security")
    public HttpEntity<?> mockSecurityPrivateUrl(){

        CommonPrincipal principal = (CommonPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        JsonizablePrincipal jPrincipal = new JsonizablePrincipal();
        jPrincipal.setDomain(principal.getDomain());
        jPrincipal.setUsername(principal.getUsername());


        for (GrantedAuthority item : principal.getAuthorities()){
            String role = item.getAuthority();
            jPrincipal.getRoles().add(role);
        }

        ObjectMapper objectMapper = MiscUtils.getObjectMapper();

        try {
            return new HttpEntity<>(objectMapper.writeValueAsString(jPrincipal));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/api/nonversion/private/mock/security")
    public HttpEntity<?> mockSecurityPublicUrl(){

        if (SecurityContextHolder.getContext().getAuthentication() != null){
            throw new RuntimeException("Il ne devrait pas avoir d'authentification pour ce type d'URL");
        }
        else{
            return new HttpEntity<>("");
        }
    }

    @GetMapping(value = "/api/v1/private/mock/security/disable")
    public HttpEntity<?> mockSecurityDisableUrl(){

        if (SecurityContextHolder.getContext().getAuthentication() != null){
            throw new RuntimeException("Il ne devrait pas avoir d'authentification pour ce type d'URL");
        }
        else{
            return new HttpEntity<>("");
        }
    }
}
