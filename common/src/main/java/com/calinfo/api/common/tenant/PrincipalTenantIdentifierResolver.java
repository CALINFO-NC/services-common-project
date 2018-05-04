package com.calinfo.api.common.tenant;

import com.calinfo.api.common.manager.PrincipalManager;
import com.calinfo.api.common.security.AbstractCommonPrincipal;
import com.calinfo.api.common.utils.SecurityUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


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
