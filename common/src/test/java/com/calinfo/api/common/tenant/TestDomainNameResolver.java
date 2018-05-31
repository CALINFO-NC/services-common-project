package com.calinfo.api.common.tenant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Created by dalexis on 31/05/2018.
 */
@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@Component
public class TestDomainNameResolver extends AbstractDefaultDomainNameResolver {
    @Override
    protected String choiceDomain(String requestDomain, String securityPrincipalDomain) {
        return StringUtils.isBlank(requestDomain) ? securityPrincipalDomain : requestDomain;
    }
}
