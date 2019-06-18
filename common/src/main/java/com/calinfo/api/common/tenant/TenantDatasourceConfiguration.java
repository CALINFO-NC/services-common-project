package com.calinfo.api.common.tenant;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


public class TenantDatasourceConfiguration {

    public static final String ENTITY_MANAGER_FACTORY_REF = "tenantEntityManagerFactory";
    public static final String TRANSACTION_MANAGER_REF = "tenantTransactionManager";
    public static final String TENANT_DATASOURCE = "tenantDataSource";

    @Autowired
    private TenantProperties tenantProperties;

    public static String getSchemaName(String prefix, String domainName){

        if (domainName == null){
            throw new NullPointerException("domainName is null");
        }

        return String.format("%s%s", prefix, domainName);
    }

    @Bean(name = TENANT_DATASOURCE)
    @ConfigurationProperties(prefix = "common.configuration.tenant.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean(name = TRANSACTION_MANAGER_REF)
    public PlatformTransactionManager tenantTransactionManager(@Qualifier(ENTITY_MANAGER_FACTORY_REF) EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = ENTITY_MANAGER_FACTORY_REF)
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier(TENANT_DATASOURCE) DataSource dataSource,
                                                                             MultiTenantConnectionProvider multiTenantConnectionProvider,
                                                                             CurrentTenantIdentifierResolver tenantIdentifierResolver) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);

        em.setPackagesToScan(tenantProperties.getDomainScanEntities());

        JpaProperties jpa = tenantProperties.getJpa();
        HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendor);

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.putAll(jpa.getProperties());
        jpaProperties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        jpaProperties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        jpaProperties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);

        jpaProperties.put(Environment.FORMAT_SQL, jpa.isShowSql());
        jpaProperties.put(Environment.HBM2DDL_AUTO, jpa.getHibernate().getDdlAuto());

        em.setJpaPropertyMap(jpaProperties);

        return em;
    }
}
