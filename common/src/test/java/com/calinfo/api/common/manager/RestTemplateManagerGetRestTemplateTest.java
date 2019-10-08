package com.calinfo.api.common.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by dalexis on 08/05/2018.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RestTemplateManagerGetRestTemplateTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private RestTemplateManager restTemplateManager;


    @Test
    public void testGetPrincipal(){
        RestTemplate restTemplate = restTemplateManager.getRestTemplate();
        Assert.assertEquals(restTemplate, restTemplateManager.getRestTemplate());
    }
}
