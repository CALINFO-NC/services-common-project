package com.calinfo.api.common.tenant;

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

import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


@ConditionalOnProperty({TenantProperties.CONDITIONNAL_PROPERTY})
@Component
public class SchemaPerTenantConnectionProvider implements MultiTenantConnectionProvider {

    @Autowired
    @Qualifier(TenantDatasourceConfiguration.TENANT_DATASOURCE)
    private transient DataSource dataSource;

    @Autowired
    private transient TenantProperties tenantProperties;

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {

        Connection connection = this.getAnyConnection();
        try (Statement statement = connection.createStatement()) {

            if (MultiTenancyStrategy.SCHEMA.name().equals(tenantProperties.getMultitenancyStrategy()))
                statement.execute(String.format("SET search_path to %s", tenantIdentifier));
            else if (MultiTenancyStrategy.DATABASE.name().equals(tenantProperties.getMultitenancyStrategy()))
                statement.execute(String.format("USE %s", tenantIdentifier));
            return connection;

        } catch (SQLException e) {
            throw new HibernateException(String.format("Could not alter JDBC connection to specified schema [%s]", tenantIdentifier), e);
        }

    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement()) {

            if (MultiTenancyStrategy.SCHEMA.name().equals(tenantProperties.getMultitenancyStrategy()))
                statement.execute(String.format("SET search_path to %s", tenantProperties.getDefaultValue()));
            else if (MultiTenancyStrategy.DATABASE.name().equals(tenantProperties.getMultitenancyStrategy()))
                statement.execute(String.format("USE %s", tenantProperties.getDefaultValue()));

        } catch (SQLException e) {
            throw new HibernateException(String.format("Could not alter JDBC connection to specified schema [%s]", tenantIdentifier), e);
        }

        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}
