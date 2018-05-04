package com.calinfo.api.common.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


/**
 * Implémentation du principal par le common
 */
public class CommonPrincipal extends AbstractCommonPrincipal {

    @Getter
    private String domain;

    public CommonPrincipal(String domain, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.domain = domain;
    }

}
