package com.calinfo.api.common.tenant;

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

    @Autowired
    private TenantApplicationState applicationState;

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

            applicationState.setStarted(true);

        }
        catch(Exception e){
            log.error(e.getMessage(), e);
            throw new TenantError(e.getMessage(), e);
        }
    }
}
