package com.calinfo.api.common.tenant;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@TestConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = TenantDatasourceConfiguration.ENTITY_MANAGER_FACTORY_REF,
        transactionManagerRef = TenantDatasourceConfiguration.TRANSACTION_MANAGER_REF,
        basePackages = "com.calinfo.api.common.tenant.repository.domain"
)
public class DomainDatasourceConfiguration extends TenantDatasourceConfiguration {
}
