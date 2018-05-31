package com.calinfo.api.common.tenant;

import com.calinfo.api.common.security.AbstractCommonPrincipal;
import com.calinfo.api.common.security.PrincipalManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDefaultDomainNameResolver implements DomainNameResolver {


    private static final Logger log = LoggerFactory.getLogger(AbstractDefaultDomainNameResolver.class);

    @Autowired
    private RequestDomainName requestDomainName;

    @Autowired
    private PrincipalManager principalManager;

    @Override
    public String getDomainName(){

        String requestDomain = null;
        String securityPrincipalDomain = null;

        try {
            if (requestDomainName != null && requestDomainName.getValue() != null) {
                requestDomain = requestDomainName.getValue();
            }
        }
        catch (BeanCreationException e){

            if (log.isDebugEnabled()){
                log.debug(e.getMessage(), e);
            }
            else {
                log.warn(e.getMessage());
            }

        }

        AbstractCommonPrincipal principal = principalManager.getPrincipal();
        if (principal != null && principal.getDomain() != null) {
            securityPrincipalDomain = principal.getDomain();
        }

        return choiceDomain(requestDomain, securityPrincipalDomain);
    }

    protected abstract String choiceDomain(String requestDomain, String securityPrincipalDomain);
}
