package com.calinfo.api.common.task;

import com.calinfo.api.common.security.AbstractCommonPrincipal;
import com.calinfo.api.common.security.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Optional;

/**
 * Created by dalexis on 31/05/2018.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TasckRunerRunTest extends AbstractTestNGSpringContextTests {

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

            return Optional.<Void>empty();
        });

        taskRunner.run( "domain", new String[]{"role1"}, () -> {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            AbstractCommonPrincipal principal = (AbstractCommonPrincipal)auth.getPrincipal();

            Assert.assertEquals(securityProperties.getSystemLogin(), principal.getUsername());
            Assert.assertEquals("domain", principal.getDomain());
            Assert.assertTrue(principal.getAuthorities().size() == 1);
            Assert.assertEquals("role1", principal.getAuthorities().iterator().next().getAuthority());

            return Optional.<Void>empty();
        });

        taskRunner.run( "domain", () -> {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            AbstractCommonPrincipal principal = (AbstractCommonPrincipal)auth.getPrincipal();

            Assert.assertEquals(securityProperties.getSystemLogin(), principal.getUsername());
            Assert.assertEquals("domain", principal.getDomain());
            Assert.assertTrue(principal.getAuthorities().size() == 0);

            return Optional.<Void>empty();
        });

        taskRunner.run( () -> {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            AbstractCommonPrincipal principal = (AbstractCommonPrincipal)auth.getPrincipal();

            Assert.assertEquals(securityProperties.getSystemLogin(), principal.getUsername());
            Assert.assertNull(principal.getDomain());
            Assert.assertTrue(principal.getAuthorities().size() == 0);

            return Optional.<Void>empty();
        });

        Authentication newAuth = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertTrue(oldAuth == newAuth);

        String strArbitraire = "arbitraire";
        Optional<String> res = taskRunner.run( () -> Optional.of(strArbitraire));

        Assert.assertTrue(res.isPresent());
        Assert.assertEquals(res.get(), strArbitraire);
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
