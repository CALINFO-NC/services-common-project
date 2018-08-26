package com.calinfo.api.common.matching;

import com.calinfo.api.common.config.ApplicationProperties;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Created by dalexis on 06/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MatchingUrlFilterEnableTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationProperties applicationProperties;

    private RestTemplate restTemplate = new RestTemplate();


    @Test
    public void callPublicUrl() throws Exception{

        String url = String.format("http://localhost:%s/api/v1//public/mock", port);

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet(url));

        Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.NOT_FOUND.value());

        url = String.format("http://localhost:%s/api/v1/public/mock", port);

        client = HttpClientBuilder.create().build();
        response = client.execute(new HttpGet(url));

        Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.OK.value());

    }

}
