package com.calinfo.api.common.tenant;

import com.calinfo.api.common.AutowiredConfig;
import com.calinfo.api.common.swagger.mock.SwaggerConfig;
import com.calinfo.api.common.utils.DatabaseUtils;
import com.calinfo.api.common.utils.LiquibaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;


@SpringBootTest(classes = {AutowiredConfig.class, GenericDatasourceConfiguration.class, DomainDatasourceConfiguration.class, SwaggerConfig.class})
@ActiveProfiles("tenant")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EntityManagerContextTest extends AbstractTestNGSpringContextTests {

    @Qualifier(TenantDatasourceConfiguration.ENTITY_MANAGER_FACTORY_REF)
    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    @Qualifier("tenantDataSource")
    private DataSource dataSource;

    @Autowired
    private TenantProperties tenantProperties;

    @BeforeMethod
    public void init(){

        System.setProperty("spring.profiles.active", "tenant");

        LiquibaseProperties liquibaseProperties = tenantProperties.getLiquibase();

        String dom = "mydom";
        String schema = String.format("%s%s", tenantProperties.getPrefix(), dom);

        DatabaseUtils.createSchema(dataSource, schema);
        LiquibaseUtils.updateSchema(dataSource, liquibaseProperties.getChangeLog(), schema);

    }

    @Test
    public void call(){

        DomainContext.setDomain("mydom");

        EntityManager em1 = EntityManagerContext.smartGet(emf);
        EntityManager em2 = EntityManagerContext.smartGet(emf);

        Assert.assertTrue(em1 == em2); // On vérifie que c'est la même instacnce

    }

}
