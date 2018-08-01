package com.calinfo.api.common.tenant;

import com.calinfo.api.common.tenant.service.TableDomainService;
import com.calinfo.api.common.tenant.service.TableGenericService;
import com.calinfo.api.common.utils.DatabaseUtils;
import com.calinfo.api.common.utils.LiquibaseUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dalexis on 10/05/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("tenant")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AllTenantWithPrincipalTest {


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

    private TenantTestPrincipal principal = null;

    private int nbDomain = 100;


    @Before
    public void init(){

        System.setProperty("spring.profiles.active", "tenant");


        LiquibaseProperties liquibaseProperties = tenantProperties.getLiquibase();

        for (int i = 0; i < nbDomain; i++){
            String dom = String.format("dom%s", i);
            String schema = String.format("%s%s", tenantProperties.getPrefix(), dom);

            DatabaseUtils.createSchema(dataSource, schema);
            LiquibaseUtils.updateSchema(dataSource, liquibaseProperties.getChangeLog(), schema);
        }



        principal = new TenantTestPrincipal();
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void callWithPrincipal(){

        Map<String, Long> mapId = new HashMap<>();
        for (int i = 0; i < nbDomain; i++){
            String dom = String.format("dom%s", i);
            String val = String.format("val%s", i);

            // Créer une donnée dans le domain i
            principal.setDomain(dom);
            long idVal = tableDomainService.create(val);
            mapId.put(val, idVal);
        }

        for (int i = 0; i < nbDomain; i++){

            String dom = String.format("dom%s", i);
            String val = String.format("val%s", i);

            principal.setDomain(dom);
            List<Long> lstVal = tableDomainService.read(val);
            Assert.assertFalse(lstVal.isEmpty());
            Assert.assertEquals(Long.valueOf(mapId.get(val)), lstVal.get(0));

            for (int j = 0; j < nbDomain; j++){

                if (i == j){
                    continue;
                }

                dom = String.format("dom%s", j);

                principal.setDomain(dom);
                lstVal = tableDomainService.read(val);
                Assert.assertTrue(lstVal.isEmpty());

            }
        }

        // Créer une donnée dans la table générique
        principal.setDomain("dom0");
        Long id = tableGenericService.create("gen1");
        Assert.assertNotNull(id);

        // Lire la donnée dans la table générique
        String val = tableGenericService.read(id);
        Assert.assertEquals("gen1", val);

        // Lire une données inexistante
        val = tableGenericService.read(id + 1);
        Assert.assertNull(val);

        // Vérifier que la donnée est tojours présente même si je change de domaine
        principal.setDomain("dom1");
        val = tableGenericService.read(id);
        Assert.assertEquals("gen1", val);
    }

}
