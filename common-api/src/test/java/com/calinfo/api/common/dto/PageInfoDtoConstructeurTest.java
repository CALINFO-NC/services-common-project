package com.calinfo.api.common.dto;

import org.springframework.data.domain.PageRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by dalexis on 21/05/2018.
 */
public class PageInfoDtoConstructeurTest {


    @Test
    public void call(){

        PageInfoDto ci = new PageInfoDto(0, 50);
        Assert.assertTrue(0 == ci.getPage());
        Assert.assertTrue(50 == ci.getLimit());

        ci = new PageInfoDto(0);
        Assert.assertTrue(0 == ci.getPage());
        Assert.assertNull(ci.getLimit());

        ci = new PageInfoDto(PageRequest.of(0, 50));
        Assert.assertTrue(0 == ci.getPage());
        Assert.assertTrue(50 == ci.getLimit());
    }
}
