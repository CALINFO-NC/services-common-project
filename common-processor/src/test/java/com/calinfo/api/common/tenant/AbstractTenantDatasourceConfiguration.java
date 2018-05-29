package com.calinfo.api.common.tenant;

@TenantJpaRepositories(
        entityManagerFactoryRef = "tenantEntityManagerFactory",
        transactionManagerRef = "tenantTransactionManager",
        basePackagesConfJsonPath = "/common/configuration/tenant/tenantScan/repository"
)
public abstract class AbstractTenantDatasourceConfiguration {

}