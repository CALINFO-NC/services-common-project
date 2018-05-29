package com.calinfo.api.common.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

public class DefaultDatasourceConfiguration {

    public static final String ENTITY_MANAGER_FACTORY_REF = "entityManagerFactory";
    public static final String TRANSACTION_MANAGER_REF = "transactionManager";

    @Autowired
    private TenantProperties tenantProperties;

    // Bug IntelliJ qui ne support pas l'injection de la source de donnée
    // https://intellij-support.jetbrains.com/hc/en-us/community/posts/207338055-Autowiring-for-Bean-Class-inspection-in-Spring-Boot-project
    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = TRANSACTION_MANAGER_REF)
    public PlatformTransactionManager transactionManager(@Qualifier(ENTITY_MANAGER_FACTORY_REF) EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Primary
    @Bean(name = ENTITY_MANAGER_FACTORY_REF)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("dataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .persistenceUnit(tenantProperties.getPersistenceName())
                .packages(tenantProperties.getGenericScanEntities())
                .build();
    }

}
