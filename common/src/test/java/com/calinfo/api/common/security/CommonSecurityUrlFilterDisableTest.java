package com.calinfo.api.common.security;

import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.mocks.JsonizablePrincipal;
import com.calinfo.api.common.utils.MiscUtils;
import com.calinfo.api.common.utils.SecurityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 06/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("disableSecurity")
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
