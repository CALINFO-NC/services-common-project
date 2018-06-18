package com.calinfo.api.common.task;

import com.calinfo.api.common.security.AbstractCommonPrincipal;
import com.calinfo.api.common.security.SecurityProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by dalexis on 31/05/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TasckRunerRunTest {

    @Autowired
    private TaskRunner taskRunner;

    @Autowired
    private SecurityProperties securityProperties;

    @Test
    public void runOk() throws TaskException {

        Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();

        taskRunner.run("login", "domain", new String[]{"role1"}, () -> {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            AbstractCommonPrincipal principal = (AbstractCommonPrincipal)auth.getPrincipal();

            Assert.assertEquals("login", principal.getUsername());
            Assert.assertEquals("domain", principal.getDomain());
            Assert.assertTrue(principal.getAuthorities().size() == 1);
            Assert.assertEquals("role1", principal.getAuthorities().iterator().next().getAuthority());
        });

        taskRunner.run( "domain", new String[]{"role1"}, () -> {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            AbstractCommonPrincipal principal = (AbstractCommonPrincipal)auth.getPrincipal();

            Assert.assertEquals(securityProperties.getSystemLogin(), principal.getUsername());
            Assert.assertEquals("domain", principal.getDomain());
            Assert.assertTrue(principal.getAuthorities().size() == 1);
            Assert.assertEquals("role1", principal.getAuthorities().iterator().next().getAuthority());
        });

        taskRunner.run( "domain", () -> {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            AbstractCommonPrincipal principal = (AbstractCommonPrincipal)auth.getPrincipal();

            Assert.assertEquals(securityProperties.getSystemLogin(), principal.getUsername());
            Assert.assertEquals("domain", principal.getDomain());
            Assert.assertTrue(principal.getAuthorities().size() == 0);
        });

        taskRunner.run( () -> {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            AbstractCommonPrincipal principal = (AbstractCommonPrincipal)auth.getPrincipal();

            Assert.assertEquals(securityProperties.getSystemLogin(), principal.getUsername());
            Assert.assertNull(principal.getDomain());
            Assert.assertTrue(principal.getAuthorities().size() == 0);
        });

        Authentication newAuth = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertTrue(oldAuth == newAuth);
    }

    @Test
    public void runKo() {

        try {
            taskRunner.run("login", "domain", new String[]{"role1"}, () -> {

                throw new TaskException();
            });

            Assert.fail("Une exception aurrait du être levée");

        } catch (TaskException e) {

            // Une exception est levée, c'est normal

        }


    }
}