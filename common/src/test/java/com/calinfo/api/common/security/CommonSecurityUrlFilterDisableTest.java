package com.calinfo.api.common.security;

import com.calinfo.api.common.utils.MiscUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Created by dalexis on 06/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("disableSecurity")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommonSecurityUrlFilterDisableTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void callPublicUrl(){

        URI uri = getUri("/api/v1/private/mock/security/disable").build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        MiscUtils.callRestApiService(restTemplate, uri, HttpMethod.GET, entity, String.class);

    }

    private UriComponentsBuilder getUri(String suffix){

        return UriComponentsBuilder.fromHttpUrl(String.format("http://localhost:%s%s", port, suffix));
    }
}
