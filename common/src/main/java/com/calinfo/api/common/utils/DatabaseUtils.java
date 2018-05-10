package com.calinfo.api.common.utils;

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


        } catch (SQLException e) {
            throw new ApplicationErrorException(e);
        }
    }
}