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

/**
 * Created by dalexis on 31/05/2018.
 */
@SpringBootTest(classes = {AutowiredConfig.class, GenericDatasourceConfiguration.class, DomainDatasourceConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ContextRunerRunTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ContextRunner contextRunner;


    @Test
    public void runOk() {

        Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();


        contextRunner.run(ContextParam
                .builder()
                .authentication(ContextParam.authenticationFromUserRoles("login", new String[]{"role1"}))
                .domain("domain")
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
            contextRunner.run(ContextParam
                    .builder()
                    .authentication(ContextParam.authenticationFromUserRoles("login", new String[]{"role1"}))
                    .domain("domain")
                    .build(), () -> {

                throw new ApplicationErrorException();
            });

            Assert.fail("Une exception aurrait du être levée");

        } catch (ApplicationErrorException e) {

            // Une exception est levée, c'est normal

        }


    }
}
