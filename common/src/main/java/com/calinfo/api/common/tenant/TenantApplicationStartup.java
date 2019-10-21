package com.calinfo.api.common.tenant;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 CALINFO
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
import com.calinfo.api.common.utils.LiquibaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by dalexis on 05/01/2018.
 */

@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(TenantApplicationStartup.class);

    @Autowired
    private TenantProperties tenantProperties;

    @Autowired
    @Qualifier("tenantDataSource")
    private DataSource dataSource;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Transactional
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        LiquibaseProperties liquibaseProperties = tenantProperties.getLiquibase();

        // On ne fait la mise à jour sur les schémas uniquement si c'est configuré comme cela
        if (!liquibaseProperties.isEnabled()){
            return;
        }

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {

            // Parcours des schémas
            DatabaseMetaData meta = connection.getMetaData();
            try(ResultSet res = meta.getSchemas()) {
                while (res.next()) {

                    // Mise à jour du schema
                    String schemaName = res.getString("TABLE_SCHEM");

                    if (schemaName.startsWith(tenantProperties.getPrefix()) && !schemaName.equals(tenantProperties.getDefaultValue())) {
                        LiquibaseUtils.updateSchema(dataSource, liquibaseProperties.getChangeLog(), schemaName);
                    }
                }
            }

            ConfigurableApplicationContext ctx = applicationReadyEvent.getApplicationContext();
            if (log.isInfoEnabled()) {
                log.info(ctx.getApplicationName().concat(" : ").concat(applicationProperties.getName()).concat(" : Système démarré"));
            }

        }
        catch(Exception e){
            throw new TenantError(e.getMessage(), e);
        }
    }
}
