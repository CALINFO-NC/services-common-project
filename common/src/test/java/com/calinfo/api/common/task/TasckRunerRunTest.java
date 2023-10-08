package com.calinfo.api.common.task;

import com.calinfo.api.common.AutowiredConfig;
import com.calinfo.api.common.domain.DomainContext;
import com.calinfo.api.common.ex.ApplicationErrorException;
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
import java.util.function.Supplier;

/**
 * Created by dalexis on 31/05/2018.
 */
@SpringBootTest(classes = {AutowiredConfig.class, GenericDatasourceConfiguration.class, DomainDatasourceConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TasckRunerRunTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private TaskRunner taskRunner;


    @Test
    public void runOk() {

        Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();


        taskRunner.run(TaskParam
                .builder()
                .username("login")
                .domain("domain")
                .roles(new String[]{"role1"})
                .build(), () -> {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            Assert.assertEquals(auth.getName(), "login");
            Assert.assertEquals(DomainContext.getDomain(), "domain");
            Assert.assertTrue(auth.getAuthorities().size() == 1);
            Assert.assertEquals(auth.getAuthorities().iterator().next().getAuthority(), "role1");

            return null;
        });

        Authentication newAuth = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertTrue(oldAuth == newAuth);
    }

    @Test
    public void runKo() {

        try {
            taskRunner.run(TaskParam
                    .builder()
                    .username("login")
                    .domain("domain")
                    .roles(new String[]{"role1"})
                    .build(), () -> {

                throw new ApplicationErrorException();
            });

            Assert.fail("Une exception aurrait du être levée");

        } catch (ApplicationErrorException e) {

            // Une exception est levée, c'est normal

        }


    }
}
