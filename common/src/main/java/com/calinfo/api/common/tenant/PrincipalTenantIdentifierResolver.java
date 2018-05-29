package com.calinfo.api.common.tenant;

import com.calinfo.api.common.security.AbstractCommonPrincipal;
import com.calinfo.api.common.security.PrincipalManager;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;



@ConditionalOnProperty("common.configuration.tenant.enable")
@Component
public class PrincipalTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    private static final Logger log = LoggerFactory.getLogger(PrincipalTenantIdentifierResolver.class);

    @Autowired
    private TenantName tenantName;

    @Autowired
    private TenantProperties tenantProperties;

    @Autowired
    private PrincipalManager principalManager;


    @Override
    public String resolveCurrentTenantIdentifier() {

        try {
            if (tenantName != null && tenantName.getValue() != null) {
                return tenantName.getValue();
            }
        }
        catch (BeanCreationException e){
            log.warn(e.getMessage(), e);
        }

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
