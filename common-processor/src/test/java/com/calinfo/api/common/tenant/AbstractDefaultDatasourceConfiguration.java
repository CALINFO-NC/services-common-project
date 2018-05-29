package com.calinfo.api.common.tenant;

@TenantJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager",
        basePackagesConfJsonPath = "/common/configuration/tenant/defaultScan/repository"
)
public abstract class AbstractDefaultDatasourceConfiguration {

}