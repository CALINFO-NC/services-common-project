package com.calinfo.api.common.tenant;

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


@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@Component
public class SchemaPerTenantConnectionProvider implements MultiTenantConnectionProvider {

    @Autowired
    @Qualifier(TenantDatasourceConfiguration.TENANT_DATASOURCE)
    private transient DataSource dataSource;

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
        try(Statement statement = connection.createStatement()) {

            statement.execute(String.format("SET search_path to %s", tenantIdentifier));
            return connection;

        } catch (SQLException e) {
            throw new HibernateException(String.format("Could not alter JDBC connection to specified schema [%s]", tenantIdentifier), e);
        }

    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {

        try(Statement statement = connection.createStatement()) {

            statement.execute("SET search_path to public");

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