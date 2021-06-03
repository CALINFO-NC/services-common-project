package com.calinfo.api.common.task;

import com.calinfo.api.common.AutowiredConfig;
import com.calinfo.api.common.tenant.DomainDatasourceConfiguration;
import com.calinfo.api.common.tenant.GenericDatasourceConfiguration;
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
@SpringBootTest(classes = {AutowiredConfig.class, GenericDatasourceConfiguration.class, DomainDatasourceConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TasckRunerRunTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private TaskRunner taskRunner;


    @Test
    public void runOk() throws TaskException {

        Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();

        taskRunner.run("login", "domain", new String[]{"role1"}, () -> {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            TaskPrincipal principal = (TaskPrincipal)auth.getPrincipal();

            Assert.assertEquals(principal.getName(), "login");
            Assert.assertEquals(principal.getDomain(), "domain");
            Assert.assertTrue(principal.getAuthorities().size() == 1);
            Assert.assertEquals(principal.getAuthorities().iterator().next().getAuthority(), "role1");

            return Optional.<Void>empty();
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
