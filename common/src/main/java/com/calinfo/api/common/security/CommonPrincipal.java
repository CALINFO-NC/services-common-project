package com.calinfo.api.common.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommonPrincipal)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CommonPrincipal that = (CommonPrincipal) o;
        return Objects.equals(getDomain(), that.getDomain());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDomain());
    }
}
