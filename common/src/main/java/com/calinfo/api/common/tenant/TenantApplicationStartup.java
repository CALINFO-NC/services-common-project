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

import com.calinfo.api.common.config.ApplicationProperties;
import com.calinfo.api.common.utils.DatabaseUtils;
import com.calinfo.api.common.utils.LiquibaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MultiTenancyStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

/**
 * Created by dalexis on 05/01/2018.
 */

@Slf4j
@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@Component
@Order(TenantApplicationStartup.ORDER)
public class TenantApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    public static final int ORDER = 10000;

    @Autowired
    private TenantProperties tenantProperties;

    @Autowired
    @Qualifier(TenantDatasourceConfiguration.TENANT_DATASOURCE)
    private DataSource dataSource;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Transactional
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        LiquibaseProperties liquibaseProperties = tenantProperties.getLiquibase();

        // On ne fait la mise à jour sur les schémas uniquement si c'est configuré comme cela
        if (!liquibaseProperties.isEnabled()) {
            return;
        }

        if (MultiTenancyStrategy.SCHEMA.name().equals(tenantProperties.getMultitenancyStrategy())) {

            DatabaseUtils.listSchema(dataSource).forEach(schemaName -> {
                if (schemaName.startsWith(tenantProperties.getPrefix()) && !schemaName.equals(tenantProperties.getDefaultValue())) {
                    LiquibaseUtils.updateSchema(dataSource, liquibaseProperties.getChangeLog(), schemaName, liquibaseProperties.getContexts());
                }
            });
        }

        if (MultiTenancyStrategy.DATABASE.name().equals(tenantProperties.getMultitenancyStrategy())) {

            DatabaseUtils.listDatabases(dataSource).forEach(databaseName -> {
                if (databaseName.startsWith(tenantProperties.getPrefix()) && !databaseName.equals(tenantProperties.getDefaultValue())) {
                    LiquibaseUtils.updateSchema(dataSource, liquibaseProperties.getChangeLog(), databaseName, liquibaseProperties.getContexts());
                }
            });
        }

        ConfigurableApplicationContext ctx = applicationReadyEvent.getApplicationContext();
        if (log.isInfoEnabled()) {
            log.info(ctx.getApplicationName().concat(" : ").concat(applicationProperties.getName()).concat(" : Système démarré"));
        }
    }
}
