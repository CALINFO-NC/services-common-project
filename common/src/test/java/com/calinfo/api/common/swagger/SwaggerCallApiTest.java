package com.calinfo.api.common.swagger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SwaggerCallApiTest {


    @LocalServerPort
    private int port;

    @Test
    public void callUrl(){

        String url = String.format("http://localhost:%s/swagger-ui.html", port);

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = null;
        try {
            response = client.execute(new HttpGet(url));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.OK.value());

    }
}
