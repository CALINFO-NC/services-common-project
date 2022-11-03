package com.calinfo.api.common.utils;

import com.calinfo.api.common.task.TaskPrincipal;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

/**
 * Created by dalexis on 23/11/2017.
 */
public class SecurityUtilsTest {


    @Test
    public void testRsaEcryptionDecryption() throws Exception{

        String publicKeyValue = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCy/bnCEtHF6im5sGUIuktbILl9vCaYMCi0CZ4wGtEUtdHZ+tsnMpeOIAusMBrmfYaq4jcUde9fWV9+vNENhpLD29x7YtR5rnaqsQv+hp+eQoWp5u3fqkRtmkjy0kqmU9idVkGjn2dA5YeXF3xxF8WnyKSwohLK8oYAxQxoaS0yuwIDAQAB";
        String privateKeyValue = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALL9ucIS0cXqKbmwZQi6S1sguX28JpgwKLQJnjAa0RS10dn62ycyl44gC6wwGuZ9hqriNxR1719ZX3680Q2GksPb3Hti1HmudqqxC/6Gn55Chanm7d+qRG2aSPLSSqZT2J1WQaOfZ0Dlh5cXfHEXxafIpLCiEsryhgDFDGhpLTK7AgMBAAECgYEAk0Ntsd8J+GvQKJaYibW8ih1Cf9BtcIku8/F11N47Z26wWUerR3S4fJahA+oQN9LPGYlFB/CAIVLG3t86oIY3+MyEnjD8gXCPaRU7Y8pFsUPjPKuYUtIqfmeoh01hEdbuczdUElrLocdwJux/dCvQ00ysxS/9QiM31sIybTMzWwECQQDZR27B/VQLgBmKgCLFVpflo0r0pWzuXUZLdLSRHFJrt3ei9SswOZEHxLALdnuclFcGI5klw6sBt1hMQOawFaH/AkEA0uOLsKqLXehQ3Nbxwf/34lUTEEPl2KFW7WjpEgsVgIM+vb9IWq15sGbD96JxP+qsO5dXKLyUUNrXS1MbJPV3RQJBAKUcnVQZOCbNH5uaJ9IiLae54RnsI803YFWyyAyFozRr5SQWfs1U0Zs/oi/zx5eDOmZV4ulJucfCFf1MTIF+zu0CQDJnkIu5N3ZKgIlIFqB3vZerHdNVZypP5ab43Dwjyg/dTrGrdm+15s/ywAQAH3FXdbMIiRyDdi+dHrgyNNqwkMECQQDGO1pttjjsaiGm1A1HknDKOdtdrpUptrFFegCUTrTV5lrsaL6izLoelXAjAeGIf2BqyQGkL3dT/uT7+0HZcZAy";

        PublicKey publicKey = SecurityUtils.getPublicKeyFromString(publicKeyValue);
        PrivateKey privateKey = SecurityUtils.getPrivateKeyFromString(privateKeyValue);


        String decryptValStr = "@Ab1!?;";
        byte[] decryptValByte = decryptValStr.getBytes();

        byte[] encryptValByte = SecurityUtils.rsaEncryption(decryptValByte, privateKey);
        byte[] decryptValByteResult = SecurityUtils.rsaDecryption(encryptValByte, publicKey);

        String decryptValStrResult = new String(decryptValByteResult);

        Assert.assertEquals(decryptValStrResult, decryptValStr);
    }

    @Test
    public void testGetRoleFromKeykloackPrincipal() throws Exception{

        String resourceName = "resourceName";
        Set<String> resourceRoles = new HashSet<>();
        resourceRoles.add("R1");
        Set<String> roles = new HashSet<>();
        roles.add("R2");

        Map<String, AccessToken.Access> resourceAccess = new HashMap<>();
        AccessToken.Access resourceAccessRole = new AccessToken.Access();
        resourceAccessRole.roles(resourceRoles);
        resourceAccess.put(resourceName, resourceAccessRole);

        AccessToken.Access access = new AccessToken.Access();
        access.roles(roles);

        AccessToken accessToken = new AccessToken();
        accessToken.setRealmAccess(access);
        accessToken.setResourceAccess(resourceAccess);

        KeycloakDeployment deployment = new KeycloakDeployment();
        deployment.setResourceName(resourceName);

        RefreshableKeycloakSecurityContext context = new RefreshableKeycloakSecurityContext(deployment, null, null, accessToken, null, null, null);

        KeycloakPrincipal<KeycloakSecurityContext> principal = new KeycloakPrincipal<>("name", context);

        List<String> resultRole = SecurityUtils.getRoleFormPrincipal(principal);
        Assert.assertEquals(resultRole.size(), 2);
        Assert.assertTrue(resultRole.contains("R1"));
        Assert.assertTrue(resultRole.contains("R2"));

    }

    @Test
    public void testGetRoleFromKTaskPrincipal() throws Exception{

        List<SimpleGrantedAuthority> lstRole = new ArrayList<>();
        lstRole.add(new SimpleGrantedAuthority("Ra"));
        lstRole.add(new SimpleGrantedAuthority("Rb"));

        TaskPrincipal principal = new TaskPrincipal("name", "domain", lstRole);

        List<String> resultRole = SecurityUtils.getRoleFormPrincipal(principal);
        Assert.assertEquals(resultRole.size(), 2);
        Assert.assertTrue(resultRole.contains("Ra"));
        Assert.assertTrue(resultRole.contains("Rb"));

    }
}
