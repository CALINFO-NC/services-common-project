package com.calinfo.api.common.manager;

import com.calinfo.api.common.AutowiredConfig;
import com.calinfo.api.common.security.PrincipalManager;
import com.calinfo.api.common.tenant.DomainDatasourceConfiguration;
import com.calinfo.api.common.tenant.GenericDatasourceConfiguration;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by dalexis on 08/05/2018.
 */
@SpringBootTest(classes = {AutowiredConfig.class, GenericDatasourceConfiguration.class, DomainDatasourceConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PrincipalManagerGetPrincipalTest extends AbstractTestNGSpringContextTests {

    private KeycloakPrincipal<RefreshableKeycloakSecurityContext> principalAjouteAuContext;

    @Autowired
    private PrincipalManager principalManager;

    @BeforeMethod
    public void before(){

        principalAjouteAuContext = new KeycloakPrincipal<>("username", new RefreshableKeycloakSecurityContext());
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalAjouteAuContext, "", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testGetPrincipal(){
        Assert.assertEquals(principalAjouteAuContext, principalManager.getPrincipal());
    }
}
