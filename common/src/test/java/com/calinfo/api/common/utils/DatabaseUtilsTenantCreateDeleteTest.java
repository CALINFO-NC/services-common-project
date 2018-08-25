package com.calinfo.api.common.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("tenant")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DatabaseUtilsTenantCreateDeleteTest {

    @Autowired
    private DataSource dataSource;

    @Qualifier("tenantDataSource")
    @Autowired
    private DataSource tenantDataSource;

    @Test
    public void call(){

        String schemaName = "schema_public";
        String tenantSchema = "schema_tenant";

        DatabaseUtils.createSchema(dataSource, schemaName);
        DatabaseUtils.createSchema(tenantDataSource, tenantSchema);
        try {
            DatabaseUtils.createSchema(tenantDataSource, tenantSchema);
        }
        catch(Exception e){
            // C'est normal s'il y a une exception
        }

        DatabaseUtils.deleteSchema(tenantDataSource, tenantSchema);

        // Pour vérifier que la base existe, on la re créé, et il ne doit pas y avoir d'excerption
        DatabaseUtils.createSchema(tenantDataSource, tenantSchema);

        try {
            // Pour vérifier que la base existe, on la re créé, et il ne doit pas y avoir d'excerption
            DatabaseUtils.createSchema(dataSource, schemaName);
        }
        catch(Exception e){
            // C'est normal s'il y a une exception
        }
    }
}