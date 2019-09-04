package com.calinfo.api.common.tenant;

import org.junit.Assert;
import org.junit.Test;

public class DomainContextTest {


    @Test
    public void call(){

        String ctx = "abitraire";
        DomainContext.setDomain(ctx);
        Assert.assertEquals(ctx, DomainContext.getDomain());

        DomainContext.setDomain(null);
        Assert.assertNull(DomainContext.getDomain());
    }
}
