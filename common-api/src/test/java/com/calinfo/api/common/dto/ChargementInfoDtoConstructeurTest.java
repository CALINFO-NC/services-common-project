package com.calinfo.api.common.dto;

import org.springframework.data.domain.PageRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by dalexis on 21/05/2018.
 */
public class ChargementInfoDtoConstructeurTest {


    @Test
    public void call(){

        ChargementInfoDto ci = new ChargementInfoDto(0, 50);
        Assert.assertTrue(0 == ci.getStart());
        Assert.assertTrue(50 == ci.getLimit());

        ci = new ChargementInfoDto(0);
        Assert.assertTrue(0 == ci.getStart());
        Assert.assertNull(ci.getLimit());

        ci = new ChargementInfoDto(PageRequest.of(0, 50));
        Assert.assertTrue(0 == ci.getStart());
        Assert.assertTrue(50 == ci.getLimit());
    }
}
