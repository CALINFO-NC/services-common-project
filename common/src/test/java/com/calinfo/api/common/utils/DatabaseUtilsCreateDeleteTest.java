package com.calinfo.api.common.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DatabaseUtilsCreateDeleteTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void call(){

        String schemaName = "mySchema";

        DatabaseUtils.createSchema(dataSource, schemaName);
        try {
            DatabaseUtils.createSchema(dataSource, schemaName);
        }
        catch(Exception e){
            // C'est normal s'il y a une exception
        }

        DatabaseUtils.deleteSchema(dataSource, schemaName);

        // Pour vérifier que la base existe, on la re créé, et il ne doit pas y avoir d'excerption
        DatabaseUtils.createSchema(dataSource, schemaName);
    }
}
