package com.calinfo.api.common.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


/**
 * Implémentation du principal par le common
 */
@EqualsAndHashCode
public class CommonPrincipal extends AbstractCommonPrincipal {

    @Getter
    private String domain;

    public CommonPrincipal(String domain, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.domain = domain;
    }
}
