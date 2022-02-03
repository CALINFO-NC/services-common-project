package com.calinfo.api.common.tenant;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2022 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class TenantDatasourceConfiguration {

    public static final String ENTITY_MANAGER_FACTORY_REF = "tenantEntityManagerFactory";
    public static final String TRANSACTION_MANAGER_REF = "tenantTransactionManager";
    public static final String TENANT_DATASOURCE = "tenantDataSource";
    public static final String ENTITY_MANAGER_REF = "tenantEntityManager";

    @Autowired
    private TenantProperties tenantProperties;

    public static String getSchemaName(String prefix, String domainName) {

        if (domainName == null) {
            throw new NullPointerException("domainName is null");
        }

        return String.format("%s%s", prefix, domainName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase(Locale.ROOT));
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
    public LocalContainerEntityManagerFactoryBean schemaTenantEntityManagerFactory(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier(TENANT_DATASOURCE) DataSource dataSource,
                                                                                   MultiTenantConnectionProvider multiTenantConnectionProvider,
                                                                                   CurrentTenantIdentifierResolver tenantIdentifierResolver) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(dataSource);

        em.setPersistenceUnitName(tenantProperties.getPersistenceName());
        em.setPackagesToScan(tenantProperties.getDomainScanEntities());

        JpaProperties jpa = tenantProperties.getJpa();
        HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendor);

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.putAll(jpa.getProperties());
        jpaProperties.put(Environment.MULTI_TENANT, tenantProperties.getMultitenancyStrategy());
        jpaProperties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        jpaProperties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);

        em.setJpaPropertyMap(jpaProperties);

        return em;
    }


    @Bean(name = ENTITY_MANAGER_REF)
    public EntityManager apoClient(@Qualifier(ENTITY_MANAGER_FACTORY_REF) EntityManagerFactory emf) {
        return (EntityManager) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class<?>[]{EntityManager.class},
                (proxy, method, args) -> method.invoke(emf.createEntityManager(), args));
    }
}
