package com.calinfo.api.common.tenant;

import com.calinfo.api.common.domain.DomainProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ConditionalOnProperty(DomainProperties.CONDITIONNAL_PROPERTY)
@TestConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = com.calinfo.api.common.domain.DomainDatasourceConfiguration.ENTITY_MANAGER_FACTORY_REF,
        transactionManagerRef = com.calinfo.api.common.domain.DomainDatasourceConfiguration.TRANSACTION_MANAGER_REF,
        basePackages = "com.calinfo.api.common.tenant.repository.domain"
)
public class DomainDatasourceConfiguration extends com.calinfo.api.common.domain.DomainDatasourceConfiguration {
}
