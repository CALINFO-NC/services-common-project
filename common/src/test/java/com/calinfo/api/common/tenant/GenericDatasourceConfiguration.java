package com.calinfo.api.common.tenant;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@TestConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = DefaultDatasourceConfiguration.ENTITY_MANAGER_FACTORY_REF,
        transactionManagerRef = DefaultDatasourceConfiguration.TRANSACTION_MANAGER_REF,
        basePackages = "com.calinfo.api.common.tenant.repository.generic"
)
public class GenericDatasourceConfiguration extends DefaultDatasourceConfiguration {
}
