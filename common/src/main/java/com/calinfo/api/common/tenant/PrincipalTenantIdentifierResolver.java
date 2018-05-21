package com.calinfo.api.common.tenant;

import com.calinfo.api.common.security.PrincipalManager;
import com.calinfo.api.common.security.AbstractCommonPrincipal;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@ConditionalOnProperty("common.configuration.tenant.enable")
@Component
public class PrincipalTenantIdentifierResolver implements CurrentTenantIdentifierResolver {


    @Autowired
    private TenantProperties tenantProperties;

    @Autowired
    private PrincipalManager principalManager;


    @Override
    public String resolveCurrentTenantIdentifier() {

        AbstractCommonPrincipal principal = principalManager.getPrincipal();
        String tenantId = null;
        if (principal != null && principal.getDomain() != null){
            tenantId = String.format("%s%s", tenantProperties.getPrefix(), principal.getDomain());
        }

        if (tenantId != null) {
            return tenantId;
        }

        return tenantProperties.getDefaultValue();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
