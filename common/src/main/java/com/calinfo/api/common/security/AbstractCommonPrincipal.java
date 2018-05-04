package com.calinfo.api.common.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


/**
 * Représentation d'un pricipal
 */
public abstract class AbstractCommonPrincipal extends User {

    public AbstractCommonPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public abstract String getDomain();

}
