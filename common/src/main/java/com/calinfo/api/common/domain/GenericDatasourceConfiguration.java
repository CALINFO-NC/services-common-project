package com.calinfo.api.common.domain;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2023 CALINFO
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

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

public class GenericDatasourceConfiguration {

    public static final String ENTITY_MANAGER_FACTORY_REF = "entityManagerFactory";
    public static final String TRANSACTION_MANAGER_REF = "transactionManager";
    public static final String DEFAULT_DATASOURCE = "dataSource";

    @Autowired
    private DomainProperties domainProperties;

    // Bug IntelliJ qui ne support pas l'injection de la source de donnée
    // https://intellij-support.jetbrains.com/hc/en-us/community/posts/207338055-Autowiring-for-Bean-Class-inspection-in-Spring-Boot-project
    @Primary
    @Bean(name = DEFAULT_DATASOURCE)
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
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier(DEFAULT_DATASOURCE) DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .persistenceUnit("default")
                .packages(domainProperties.getGenericScanEntities())
                .build();
    }

}
