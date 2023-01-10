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
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import javax.sql.DataSource;
import java.sql.Connection;

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

    public static void updateSchema(DataSource dataSource, String changelogFileConf, String schemaName, String... contexts) {

        // Création de la connection
        try (Connection con = dataSource.getConnection()) {

            String changelogName = changelogFileConf;
            if (changelogName.startsWith(CHAGELOG_LOG_CLASSPATH_REFIXE)){
                changelogName = changelogName.substring(CHAGELOG_LOG_CLASSPATH_REFIXE.length());
            }

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(con));
            database.setDefaultSchemaName(schemaName);

            try(Liquibase liquibase = new Liquibase(changelogName, new ClassLoaderResourceAccessor(), database)) {
                liquibase.update(new Contexts(contexts));
            }

        } catch (Exception e) {
            throw new ApplicationErrorException(e);
        }
    }
}
