package com.calinfo.api.common.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;



@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@Component
public class PrincipalTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Autowired
    private TenantProperties tenantProperties;

    @Override
    public String resolveCurrentTenantIdentifier() {

        String domainName = DomainContext.getDomain();

        if (domainName != null){
            return TenantDatasourceConfiguration.getSchemaName(tenantProperties.getPrefix(), domainName);
        }

        return tenantProperties.getDefaultValue();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
