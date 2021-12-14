package com.calinfo.api.common.tenant;

import com.calinfo.api.common.AutowiredConfig;
import com.calinfo.api.common.tenant.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.Future;

/**
 * Created by dalexis on 10/05/2018.
 */
@ActiveProfiles("asyncConfig")
@SpringBootTest(classes = {AutowiredConfig.class})
public class AllAsyncWithDomainTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private AsyncService asyncService;


    @Test
    public void call() throws Exception {

        String dom = "domarbitraire";
        DomainContext.setDomain(dom);
        Future<String> f = asyncService.call();

        Assert.assertEquals(dom, f.get());

    }
}
