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



@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@Component
public class PrincipalTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    private static final Logger log = LoggerFactory.getLogger(PrincipalTenantIdentifierResolver.class);

    @Autowired
    private RequestDomainName requestDomainName;

    @Autowired
    private TenantProperties tenantProperties;

    @Autowired
    private PrincipalManager principalManager;

    private String getDomainName(){

        try {
            if (requestDomainName != null && requestDomainName.getValue() != null) {
                return requestDomainName.getValue();
            }
        }
        catch (BeanCreationException e){
            log.warn(e.getMessage(), e);
        }

        AbstractCommonPrincipal principal = principalManager.getPrincipal();
        if (principal != null && principal.getDomain() != null) {
            return principal.getDomain();
        }

        return null;
    }

    @Override
    public String resolveCurrentTenantIdentifier() {

        String domainName = getDomainName();

        if (domainName != null){
            return String.format("%s%s", tenantProperties.getPrefix(), domainName);
        }

        return tenantProperties.getDefaultValue();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
