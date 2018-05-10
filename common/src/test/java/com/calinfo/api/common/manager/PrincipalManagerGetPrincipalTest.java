package com.calinfo.api.common.manager;

import com.calinfo.api.common.security.AbstractCommonPrincipal;
import com.calinfo.api.common.security.CommonPrincipal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

/**
 * Created by dalexis on 08/05/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PrincipalManagerGetPrincipalTest {

    private AbstractCommonPrincipal principalAjouteAuContext;

    @Autowired
    private PrincipalManager principalManager;

    @Before
    public void before(){

        principalAjouteAuContext = new CommonPrincipal("domain", "username", "password", new ArrayList<>());
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalAjouteAuContext, "", principalAjouteAuContext.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testGetPrincipal(){
        Assert.assertEquals(principalAjouteAuContext, principalManager.getPrincipal());
    }
}
