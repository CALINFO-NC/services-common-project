package com.calinfo.api.common.security;

import com.calinfo.api.common.config.ApplicationProperties;
import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.manager.ApiKeyManager;
import com.calinfo.api.common.mocks.JsonizablePrincipal;
import com.calinfo.api.common.mocks.MockApiKeyManager;
import com.calinfo.api.common.utils.DateUtils;
import com.calinfo.api.common.utils.MiscUtils;
import com.calinfo.api.common.utils.SecurityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Created by dalexis on 06/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"enableSecurity", "apiKeyManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommonSecurityUrlFilterEnableTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ApiKeyManager apiKeyManager;

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

        JwtUser user = new JwtUser();
        user.setLogin(login);
        user.setDomain(applicationProperties.getId());
        List<String> otherRoles = user.getRoles();
        otherRoles.add("role1");
        otherRoles.add("role2");

        String token = SecurityUtils.getJwtFromUser(PRIVATE_KEY_VALUE, 60l * 1000l, user);

        headers = new HttpHeaders();
        headers.add(CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME, CommonSecurityUrlFilter.BEARER_PREFIX + token);
        entity = new HttpEntity<>("", headers);

        JsonizablePrincipal principal = MiscUtils.callRestApiService(restTemplate, uri, HttpMethod.GET, entity, JsonizablePrincipal.class);

        Assert.assertEquals(principal.getUsername(), login);
        Assert.assertEquals(2, principal.getRoles().size());
        Assert.assertTrue(principal.getRoles().contains("role1"));
        Assert.assertTrue(principal.getRoles().contains("role2"));
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

    @Test
    public void callPublicUrlAndVerifyTokenResponse() throws Exception{

        URI uri = getUri("/api/v1/private/mock/security").build().encode().toUri();

        String login = "toto@gmail.com";

        JwtUser user = new JwtUser();
        user.setLogin(login);
        user.setDomain(applicationProperties.getId());

        String token = SecurityUtils.getJwtFromUser(PRIVATE_KEY_VALUE, 60l * 1000l, user);

        HttpHeaders headers = new HttpHeaders();
        headers.add(CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME, CommonSecurityUrlFilter.BEARER_PREFIX + token);
        HttpEntity entity = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        Assert.assertTrue(response.getHeaders().get(CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME).size() == 1);
        Assert.assertEquals(String.format("%s%s", CommonSecurityUrlFilter.BEARER_PREFIX, token), response.getHeaders().get(CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME).get(0));


        // Creation d'un token périmé
        MockApiKeyManager mockApiKeyManager = (MockApiKeyManager)apiKeyManager;
        System.setProperty(DateUtils.SYSTEM_PROPERTIE_DATE_SYSTEM, "1990-04-01T13:38:09-08:00");
        token = SecurityUtils.getJwtFromUser(PRIVATE_KEY_VALUE, 60l * 1000l, user);
        System.clearProperty(DateUtils.SYSTEM_PROPERTIE_DATE_SYSTEM);

        // Mise en place du nouveau token qui remplacera le token périmé
        String newToken = SecurityUtils.getJwtFromUser(PRIVATE_KEY_VALUE, 60l * 1000l, user);
        mockApiKeyManager.setNewToken(newToken);

        // Création d'une clé permettant de replacer le nouveau token
        String apiKey = SecurityUtils.getJwtFromUser(PRIVATE_KEY_VALUE, 60l * 1000l, user);

        headers = new HttpHeaders();
        headers.add(CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME, CommonSecurityUrlFilter.BEARER_PREFIX + token);
        headers.add(CommonSecurityUrlFilter.HEADER_API_KEY, apiKey);
        entity = new HttpEntity<>("", headers);

        response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        Assert.assertTrue(response.getHeaders().get(CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME).size() == 1);
        Assert.assertNotEquals(String.format("%s%s", CommonSecurityUrlFilter.BEARER_PREFIX, token), response.getHeaders().get(CommonSecurityUrlFilter.HEADER_AUTHORIZATION_NAME).get(0));
    }
}
