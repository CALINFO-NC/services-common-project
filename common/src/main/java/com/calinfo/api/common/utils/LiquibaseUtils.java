package com.calinfo.api.common.utils;

import com.calinfo.api.common.ex.ApplicationErrorException;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by dalexis on 05/01/2018.
 */
public class LiquibaseUtils {

    private static final String CHAGELOG_LOG_CLASSPATH_REFIXE = "classpath:";

    /**
     * Permet déviter que l'on puisse instancier une classe utilitaire
     */
    private LiquibaseUtils(){
    }

    public static void updateSchema(DataSource dataSource, String changelogFileConf, String schemaName) {

        // Création de la connection
        try (Connection con = dataSource.getConnection()) {

            String changelogName = changelogFileConf;
            if (changelogName.startsWith(CHAGELOG_LOG_CLASSPATH_REFIXE)){
                changelogName = changelogName.substring(CHAGELOG_LOG_CLASSPATH_REFIXE.length());
            }

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(con));
            database.setDefaultSchemaName(schemaName);

            Liquibase liquibase = new Liquibase(changelogName,
                    new ClassLoaderResourceAccessor(), database);


            liquibase.update(new Contexts());


        } catch (SQLException | LiquibaseException e) {
            throw new ApplicationErrorException(e);
        }
    }
}
