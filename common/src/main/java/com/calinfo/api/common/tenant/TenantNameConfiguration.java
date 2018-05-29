package com.calinfo.api.common.tenant;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@Configuration
public class TenantNameConfiguration {

    @Bean(name = "requestDomainName")
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public RequestDomainName domainName() {

        RequestDomainName requestDomainName = new RequestDomainName();

        return requestDomainName;
    }
}