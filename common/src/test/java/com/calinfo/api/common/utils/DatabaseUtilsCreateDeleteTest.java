package com.calinfo.api.common.utils;

import com.calinfo.api.common.AutowiredConfig;
import com.calinfo.api.common.swagger.mock.SwaggerConfig;
import com.calinfo.api.common.tenant.DomainDatasourceConfiguration;
import com.calinfo.api.common.tenant.GenericDatasourceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.sql.DataSource;

@SpringBootTest(classes = {AutowiredConfig.class, GenericDatasourceConfiguration.class, DomainDatasourceConfiguration.class, SwaggerConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DatabaseUtilsCreateDeleteTest extends AbstractTestNGSpringContextTests {

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
