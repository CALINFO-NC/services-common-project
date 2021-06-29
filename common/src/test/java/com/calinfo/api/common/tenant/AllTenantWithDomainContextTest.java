package com.calinfo.api.common.tenant;

import com.calinfo.api.common.AutowiredConfig;
import com.calinfo.api.common.tenant.service.TableDomainService;
import com.calinfo.api.common.tenant.service.TableGenericService;
import com.calinfo.api.common.utils.DatabaseUtils;
import com.calinfo.api.common.utils.LiquibaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dalexis on 10/05/2018.
 */
@SpringBootTest(classes = {AutowiredConfig.class, GenericDatasourceConfiguration.class, DomainDatasourceConfiguration.class})
@ActiveProfiles("tenant")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AllTenantWithDomainContextTest extends AbstractTestNGSpringContextTests {


    @Autowired
    private TenantProperties tenantProperties;

    @Autowired
    @Qualifier("tenantDataSource")
    private DataSource dataSource;

    @Autowired
    private TableDomainService tableDomainService;

    @Autowired
    private TableGenericService tableGenericService;

    @Autowired
    private WebApplicationContext context;

    private int nbDomain = 10;


    @BeforeMethod
    public void init(){

        System.setProperty("spring.profiles.active", "tenant");


        LiquibaseProperties liquibaseProperties = tenantProperties.getLiquibase();

        for (int i = 0; i < nbDomain; i++){
            String dom = String.format("mydom%s", i);
            String schema = String.format("%s%s", tenantProperties.getPrefix(), dom);

            DatabaseUtils.createSchemaOrDatabase(dataSource, schema);
            LiquibaseUtils.updateSchema(dataSource, liquibaseProperties.getChangeLog(), schema);
        }
    }

    @Test
    public void callWithPrincipal(){

        Map<String, Long> mapId = new HashMap<>();
        for (int i = 0; i < nbDomain; i++){
            String dom = String.format("mydom%s", i);
            String val = String.format("val%s", i);

            // Créer une donnée dans le domain i
            DomainContext.setDomain(dom);
            long idVal = tableDomainService.create(val);
            mapId.put(val, idVal);
        }

        for (int i = 0; i < nbDomain; i++){

            String dom = String.format("mydom%s", i);
            String val = String.format("val%s", i);

            DomainContext.setDomain(dom);
            List<Long> lstVal = tableDomainService.read(val);
            Assert.assertFalse(lstVal.isEmpty());
            Assert.assertEquals(Long.valueOf(mapId.get(val)), lstVal.get(0));

            for (int j = 0; j < nbDomain; j++){

                if (i == j){
                    continue;
                }

                dom = String.format("mydom%s", j);

                DomainContext.setDomain(dom);
                lstVal = tableDomainService.read(val);
                Assert.assertTrue(lstVal.isEmpty());

            }
        }

        // Créer une donnée dans la table générique
        DomainContext.setDomain("mydom0");
        Long id = tableGenericService.create("gen1");
        Assert.assertNotNull(id);

        // Lire la donnée dans la table générique
        String val = tableGenericService.read(id);
        Assert.assertEquals(val, "gen1");

        // Lire une données inexistante
        val = tableGenericService.read(id + 1);
        Assert.assertNull(val);

        // Vérifier que la donnée est tojours présente même si je change de domaine
        DomainContext.setDomain("mydom1");
        val = tableGenericService.read(id);
        Assert.assertEquals(val, "gen1");
    }

}
