package com.calinfo.api.common.tenant;

import com.calinfo.api.common.security.AbstractCommonPrincipal;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Created by dalexis on 10/05/2018.
 */
public class TenantTestPrincipal extends AbstractCommonPrincipal {

    @Getter
    @Setter
    private String domain;

    public TenantTestPrincipal() {
        super("username", "password", new ArrayList<>());
    }
}
