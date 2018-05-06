package com.calinfo.api.common.security;

import com.calinfo.api.common.config.ApplicationProperties;
import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.mocks.JsonizablePrincipal;
import com.calinfo.api.common.utils.MiscUtils;
import com.calinfo.api.common.utils.SecurityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@ActiveProfiles("enableSecurity")
public class CommonSecurityUrlFilterEnableTest {

    @LocalServerPort
    private int port;

    @Autowired
    ApplicationProperties applicationProperties;


    private RestTemplate restTemplate = new RestTemplate();

    private static final String PRIVATE_KEY_VALUE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALL9ucIS0cXqKbmwZQi6S1sguX28JpgwKLQJnjAa0RS10dn62ycyl44gC6wwGuZ9hqriNxR1719ZX3680Q2GksPb3Hti1HmudqqxC/6Gn55Chanm7d+qRG2aSPLSSqZT2J1WQaOfZ0Dlh5cXfHEXxafIpLCiEsryhgDFDGhpLTK7AgMBAAECgYEAk0Ntsd8J+GvQKJaYibW8ih1Cf9BtcIku8/F11N47Z26wWUerR3S4fJahA+oQN9LPGYlFB/CAIVLG3t86oIY3+MyEnjD8gXCPaRU7Y8pFsUPjPKuYUtIqfmeoh01hEdbuczdUElrLocdwJux/dCvQ00ysxS/9QiM31sIybTMzWwECQQDZR27B/VQLgBmKgCLFVpflo0r0pWzuXUZLdLSRHFJrt3ei9SswOZEHxLALdnuclFcGI5klw6sBt1hMQOawFaH/AkEA0uOLsKqLXehQ3Nbxwf/34lUTEEPl2KFW7WjpEgsVgIM+vb9IWq15sGbD96JxP+qsO5dXKLyUUNrXS1MbJPV3RQJBAKUcnVQZOCbNH5uaJ9IiLae54RnsI803YFWyyAyFozRr5SQWfs1U0Zs/oi/zx5eDOmZV4ulJucfCFf1MTIF+zu0CQDJnkIu5N3ZKgIlIFqB3vZerHdNVZypP5ab43Dwjyg/dTrGrdm+15s/ywAQAH3FXdbMIiRyDdi+dHrgyNNqwkMECQQDGO1pttjjsaiGm1A1HknDKOdtdrpUptrFFegCUTrTV5lrsaL6izLoelXAjAeGIf2BqyQGkL3dT/uT7+0HZcZAy";

    @Test
    public void callPrivateUrl() throws Exception{

        URI uri = getUri("/api/v1/private/mock/security").build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        // Verifier qu'il y a une erreur FORBIDEN
        try{
            MiscUtils.callRestApiService(restTemplate, uri, HttpMethod.GET, entity, JsonizablePrincipal.class);
            Assert.fail("L'appel à une URL privée aurais du planté");
        }
        catch(MessageStatusException e){

            if (e.getStatus() != HttpStatus.FORBIDDEN)
                Assert.fail(e.getMessage());
        }


        // Vérifier que l'authentification se passe bien
        String login = "toto@gmail.com";
        List<String> roles = new ArrayList<>();
        roles.add("role1");
        roles.add("role2");

        JwtUser user = new JwtUser();
        user.setLogin(login);
        user.setDomain(applicationProperties.getId());
        user.getRolesApp().put(applicationProperties.getId(), roles);
        List<String> otherRoles = new ArrayList<>();
        otherRoles.add("app1Role1");
        otherRoles.add("app1Role2");
        user.getRolesApp().put(applicationProperties.getId() + "TOTO", otherRoles);

        String token = SecurityUtils.getJwtFromUser(PRIVATE_KEY_VALUE, 60l * 1000l, user);


        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        entity = new HttpEntity<>("", headers);

        JsonizablePrincipal principal = MiscUtils.callRestApiService(restTemplate, uri, HttpMethod.GET, entity, JsonizablePrincipal.class);

        Assert.assertEquals(principal.getUsername(), login);
        Assert.assertEquals(principal.getRoles().size(), roles.size());

        for (String role : principal.getRoles()){
            Assert.assertTrue(roles.contains(role));
        }
    }

    @Test
    public void callPublicUrl(){

        URI uri = getUri("/api/nonversion/private/mock/security").build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        MiscUtils.callRestApiService(restTemplate, uri, HttpMethod.GET, entity, String.class);

    }

    private UriComponentsBuilder getUri(String suffix){

        return UriComponentsBuilder.fromHttpUrl(String.format("http://localhost:%s%s", port, suffix));
    }
}
