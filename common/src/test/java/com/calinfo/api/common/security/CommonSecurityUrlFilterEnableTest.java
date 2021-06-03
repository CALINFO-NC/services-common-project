package com.calinfo.api.common.security;

import com.calinfo.api.common.AutowiredConfig;
import com.calinfo.api.common.tenant.DomainDatasourceConfiguration;
import com.calinfo.api.common.tenant.GenericDatasourceConfiguration;
import com.calinfo.api.common.utils.MiscUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.testng.annotations.Test;

import java.net.URI;

/**
 * Created by dalexis on 06/01/2018.
 */
@SpringBootTest(classes = {AutowiredConfig.class, GenericDatasourceConfiguration.class, DomainDatasourceConfiguration.class} , webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"enableSecurity"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommonSecurityUrlFilterEnableTest extends AbstractTestNGSpringContextTests {

    @LocalServerPort
    private int port;


    private RestTemplate restTemplate = new RestTemplate();

    private static final String PRIVATE_KEY_VALUE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALL9ucIS0cXqKbmwZQi6S1sguX28JpgwKLQJnjAa0RS10dn62ycyl44gC6wwGuZ9hqriNxR1719ZX3680Q2GksPb3Hti1HmudqqxC/6Gn55Chanm7d+qRG2aSPLSSqZT2J1WQaOfZ0Dlh5cXfHEXxafIpLCiEsryhgDFDGhpLTK7AgMBAAECgYEAk0Ntsd8J+GvQKJaYibW8ih1Cf9BtcIku8/F11N47Z26wWUerR3S4fJahA+oQN9LPGYlFB/CAIVLG3t86oIY3+MyEnjD8gXCPaRU7Y8pFsUPjPKuYUtIqfmeoh01hEdbuczdUElrLocdwJux/dCvQ00ysxS/9QiM31sIybTMzWwECQQDZR27B/VQLgBmKgCLFVpflo0r0pWzuXUZLdLSRHFJrt3ei9SswOZEHxLALdnuclFcGI5klw6sBt1hMQOawFaH/AkEA0uOLsKqLXehQ3Nbxwf/34lUTEEPl2KFW7WjpEgsVgIM+vb9IWq15sGbD96JxP+qsO5dXKLyUUNrXS1MbJPV3RQJBAKUcnVQZOCbNH5uaJ9IiLae54RnsI803YFWyyAyFozRr5SQWfs1U0Zs/oi/zx5eDOmZV4ulJucfCFf1MTIF+zu0CQDJnkIu5N3ZKgIlIFqB3vZerHdNVZypP5ab43Dwjyg/dTrGrdm+15s/ywAQAH3FXdbMIiRyDdi+dHrgyNNqwkMECQQDGO1pttjjsaiGm1A1HknDKOdtdrpUptrFFegCUTrTV5lrsaL6izLoelXAjAeGIf2BqyQGkL3dT/uT7+0HZcZAy";

    @Test
    public void callPublicUrl(){

        URI uri = getUri("/api/nonversion/private/mock/security").build().encode().toUri();

        MiscUtils.callRestApiService(restTemplate, uri, HttpMethod.GET, HttpEntity.EMPTY, String.class);

    }

    private UriComponentsBuilder getUri(String suffix){

        return UriComponentsBuilder.fromHttpUrl(String.format("http://localhost:%s%s", port, suffix));
    }
}
