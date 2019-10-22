package com.calinfo.api.common.utils;

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

import com.calinfo.api.common.ex.ApplicationErrorException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by dalexis on 11/05/2018.
 */
public class DatabaseUtils {

    private DatabaseUtils(){
    }

    public static void createSchema(DataSource dataSource, String schemaName){

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

    public static void deleteSchema(DataSource dataSource, String schemaName){

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
}
