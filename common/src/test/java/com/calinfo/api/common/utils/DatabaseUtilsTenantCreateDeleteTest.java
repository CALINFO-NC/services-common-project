package com.calinfo.api.common.utils;

import com.calinfo.api.common.AutowiredConfig;
import com.calinfo.api.common.tenant.DomainDatasourceConfiguration;
import com.calinfo.api.common.tenant.GenericDatasourceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.sql.DataSource;

@SpringBootTest(classes = {AutowiredConfig.class, GenericDatasourceConfiguration.class, DomainDatasourceConfiguration.class})
@ActiveProfiles("tenant")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DatabaseUtilsTenantCreateDeleteTest extends AbstractTestNGSpringContextTests {

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
