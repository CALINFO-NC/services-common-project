package com.calinfo.api.common.tenant;

import com.calinfo.api.common.manager.RestTemplateManager;
import com.calinfo.api.common.utils.DatabaseUtils;
import com.calinfo.api.common.utils.LiquibaseUtils;
import com.calinfo.api.common.utils.MiscUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.net.URI;
import java.util.List;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by dalexis on 10/05/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("tenant")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AllTenantWithFilterTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TenantProperties tenantProperties;

    @Autowired
    @Qualifier("tenantDataSource")
    private DataSource dataSource;

    @Autowired
    private RestTemplateManager restTemplateManager;

    @Autowired
    private WebApplicationContext context;

    private String schema1 = null;
    private String schema2 = null;

    private MockMvc mockMvc;

    @After
    public  void downUp(){
        this.mockMvc = null;
    }

    @Before
    public void init(){

        String domain1 = "dom1";
        String domain2 = "dom2";

        schema1 = String.format("%s%s", tenantProperties.getPrefix(), domain1);
        schema2 = String.format("%s%s", tenantProperties.getPrefix(), domain2);

        LiquibaseProperties liquibaseProperties = tenantProperties.getLiquibase();

        DatabaseUtils.createSchema(dataSource, schema1);
        LiquibaseUtils.updateSchema(dataSource, liquibaseProperties.getChangeLog(), schema1);

        DatabaseUtils.createSchema(dataSource, schema2);
        LiquibaseUtils.updateSchema(dataSource, liquibaseProperties.getChangeLog(), schema2);

        mockMvc = webAppContextSetup(this.context).build();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + this.port + uri;
    }

    private String callUrl(String url, HttpMethod method, String tenant){
        RestTemplate restTemplate = restTemplateManager.getRestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURLWithPort(url));
        URI uri = builder.build().encode().toUri();
        HttpHeaders headers = new HttpHeaders();

        if (tenant != null)
            headers.set("tenant", tenant);

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        return restTemplate.exchange(uri, method, entity, String.class).getBody();
    }

    @Test
    public void callWithRequestFilter() throws Exception {

        ObjectMapper objectMapper = MiscUtils.getObjectMapper();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, Long.class);

        // Créer une donnée dans le domain 1
        String response = callUrl("/mock/tenant/domain/val1", HttpMethod.POST, schema1);
        Long idVal1 = objectMapper.readValue(response, Long.class);

        // Créer une donnée dans le domain 2
        response = callUrl("/mock/tenant/domain/val2", HttpMethod.POST, schema2);
        Long idVal2 = objectMapper.readValue(response, Long.class);

        // Vérifier que val 1 n'est pas dans domain 2 mais est bien dans domain 1
        response = callUrl("/mock/tenant/domain/val1", HttpMethod.GET, schema2);
        List<Long> lstVal1 = objectMapper.readValue(response, type);

        Assert.assertTrue(lstVal1.isEmpty());

        response = callUrl("/mock/tenant/domain/val1", HttpMethod.GET, schema1);
        lstVal1 = objectMapper.readValue(response, type);

        Assert.assertFalse(lstVal1.isEmpty());
        Assert.assertEquals(Long.valueOf(idVal1), lstVal1.get(0));

        // Cérifier que val 2 n'est pas dans domain 1 mais est bien dans domain 2
        response = callUrl("/mock/tenant/domain/val2", HttpMethod.GET, schema1);
        List<Long> lstVal2 = objectMapper.readValue(response, type);

        Assert.assertTrue(lstVal2.isEmpty());

        response = callUrl("/mock/tenant/domain/val2", HttpMethod.GET, schema2);
        lstVal2 = objectMapper.readValue(response, type);

        Assert.assertFalse(lstVal2.isEmpty());
        Assert.assertEquals(Long.valueOf(idVal2), lstVal2.get(0));

        // Créer une donnée dans la table générique
        response = callUrl("/mock/tenant/generic/gen1", HttpMethod.POST, null);
        Long id = objectMapper.readValue(response, Long.class);
        Assert.assertNotNull(id);

        // Lire la donnée dans la table générique
        response = callUrl(String.format("/mock/tenant/generic/%s", id), HttpMethod.GET, null);
        String val = objectMapper.readValue(response, String.class);
        Assert.assertEquals("gen1", val);

        // Lire une données inexistante
        val = callUrl(String.format("/mock/tenant/generic/%s", (id + 1)), HttpMethod.GET, null);
        Assert.assertNull(val);

        // Vérifier que la donnée est tojours présente même si je change de domaine
        response = callUrl(String.format("/mock/tenant/generic/%s", id), HttpMethod.GET, schema1);
        val = objectMapper.readValue(response, String.class);
        Assert.assertEquals("gen1", val);
    }
}
