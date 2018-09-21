package com.calinfo.api.common.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Lazy
@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@Component
public class PrincipalTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Autowired
    private TenantProperties tenantProperties;

    @Autowired
    private DomainNameResolver domainNameResolver;


    @Override
    public String resolveCurrentTenantIdentifier() {

        String domainName = domainNameResolver.getDomainName();

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
