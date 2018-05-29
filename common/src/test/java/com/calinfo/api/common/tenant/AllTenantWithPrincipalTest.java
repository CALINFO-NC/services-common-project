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
import java.util.List;

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

    private String domain1 = "dom1";
    private String domain2 = "dom2";


    @Before
    public void init(){

        System.setProperty("spring.profiles.active", "tenant");


        String schema1 = String.format("%s%s", tenantProperties.getPrefix(), domain1);
        String schema2 = String.format("%s%s", tenantProperties.getPrefix(), domain2);

        principal = new TenantTestPrincipal();
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LiquibaseProperties liquibaseProperties = tenantProperties.getLiquibase();

        DatabaseUtils.createSchema(dataSource, schema1);
        LiquibaseUtils.updateSchema(dataSource, liquibaseProperties.getChangeLog(), schema1);

        DatabaseUtils.createSchema(dataSource, schema2);
        LiquibaseUtils.updateSchema(dataSource, liquibaseProperties.getChangeLog(), schema2);
    }

    @Test
    public void callWithPrincipal(){

        // Créer une donnée dans le domain 1
        principal.setDomain(domain1);
        long idVal1 = tableDomainService.create("val1");

        // Créer une donnée dans le domain 2
        principal.setDomain(domain2);
        long idVal2 = tableDomainService.create("val2");

        // Vérifier que val 1 n'est pas dans domain 2 mais est bien dans domain 1
        principal.setDomain(domain2);
        List<Long> lstVal1 = tableDomainService.read("val1");
        Assert.assertTrue(lstVal1.isEmpty());
        principal.setDomain(domain1);
        lstVal1 = tableDomainService.read("val1");
        Assert.assertFalse(lstVal1.isEmpty());
        Assert.assertEquals(Long.valueOf(idVal1), lstVal1.get(0));

        // Cérifier que val 2 n'est pas dans domain 1 mais est bien dans domain 2
        principal.setDomain(domain1);
        List<Long> lstVal2 = tableDomainService.read("val2");
        Assert.assertTrue(lstVal2.isEmpty());
        principal.setDomain(domain2);
        lstVal2 = tableDomainService.read("val2");
        Assert.assertFalse(lstVal2.isEmpty());
        Assert.assertEquals(Long.valueOf(idVal2), lstVal2.get(0));

        // Créer une donnée dans la table générique
        Long id = tableGenericService.create("gen1");
        Assert.assertNotNull(id);

        // Lire la donnée dans la table générique
        String val = tableGenericService.read(id);
        Assert.assertEquals("gen1", val);

        // Lire une données inexistante
        val = tableGenericService.read(id + 1);
        Assert.assertNull(val);

        // Vérifier que la donnée est tojours présente même si je change de domaine
        principal.setDomain(domain1);
        val = tableGenericService.read(id);
        Assert.assertEquals("gen1", val);
    }

}
