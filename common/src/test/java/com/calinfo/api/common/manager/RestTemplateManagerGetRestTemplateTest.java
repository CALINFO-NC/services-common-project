package com.calinfo.api.common.manager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Created by dalexis on 08/05/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RestTemplateManagerGetRestTemplateTest {

    @Autowired
    private RestTemplateManager restTemplateManager;


    @Test
    public void testGetPrincipal(){
        RestTemplate restTemplate = restTemplateManager.getRestTemplate();
        Assert.assertEquals(restTemplate, restTemplateManager.getRestTemplate());
    }
}
