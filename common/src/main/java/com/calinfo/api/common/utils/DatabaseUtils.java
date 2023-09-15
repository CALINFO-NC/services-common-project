package com.calinfo.api.common.utils;

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

import com.calinfo.api.common.ex.ApplicationErrorException;
import com.calinfo.api.common.domain.DomainError;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 11/05/2018.
 */
public class DatabaseUtils {

    private DatabaseUtils() {
    }

    /**
     * Si la source de donnée accepte les schémas (postgres) un schema sera créé
     * Sinon, une BDD sera créée
     *
     * @param dataSource
     * @param schemaName
     */
    public static void createSchemaOrDatabase(DataSource dataSource, String schemaName) {

        // Création de la connection
        try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement()) {

            statement.executeUpdate(String.format("CREATE SCHEMA %s", schemaName));

            if (!con.getAutoCommit()) {
                con.commit();
            }

        } catch (SQLException e) {
            throw new ApplicationErrorException(e);
        }
    }

    public static void deleteSchema(DataSource dataSource, String schemaName) {

        // Création de la connection
        try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement()) {

            statement.executeUpdate(String.format("DROP SCHEMA %s CASCADE", schemaName));

            if (!con.getAutoCommit()) {
                con.commit();
            }

        } catch (SQLException e) {
            throw new ApplicationErrorException(e);
        }
    }

    public static List<String> listSchema(DataSource dataSource) {

        List<String> result = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            // Parcours des schémas
            DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet res = meta.getSchemas()) {
                while (res.next()) {

                    // Récupération du schéma
                    String schemaName = res.getString("TABLE_SCHEM");
                    result.add(schemaName);
                }
            }

        } catch (Exception e) {
            throw new DomainError(e.getMessage(), e);
        }

        return result;
    }

    public static List<String> listDatabases(DataSource dataSource) {
        List<String> result = new ArrayList<>();

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {

            ResultSet databases = statement.executeQuery("SHOW DATABASES");

            while (databases.next()) {
                result.add(databases.getString(1));
            }

        } catch (SQLException e) {
            throw new ApplicationErrorException(e);
        }

        return result;

    }

}
